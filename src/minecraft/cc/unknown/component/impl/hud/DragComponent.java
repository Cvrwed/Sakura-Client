package cc.unknown.component.impl.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import static cc.unknown.util.animation.Easing.LINEAR;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

import cc.unknown.Sakura;
import cc.unknown.component.impl.Component;
import cc.unknown.component.impl.hud.dragcomponent.api.Orientation;
import cc.unknown.component.impl.hud.dragcomponent.api.Snap;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.GuiClickEvent;
import cc.unknown.event.impl.input.GuiMouseReleaseEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.util.MouseUtil;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.gui.GUIUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.Value;
import cc.unknown.value.impl.DragValue;

public class DragComponent extends Component {

    private static DragValue selectedValue = null;
    private static Vector2d offset;
    private static final ArrayList<Module> modules = new ArrayList<>();
    private static final Animation animationAlpha = new Animation(LINEAR, 600);
    public static final StopWatch closeStopWatch = new StopWatch(), stopWatch = new StopWatch();

    public static ArrayList<Snap> snaps = new ArrayList<>();
    public static Snap selected;

    @EventLink(value = -2) // Must be rendered after the LayerManager
    public final Listener<Render2DEvent> onRender2D = event -> {
        try {
            final ScaledResolution scaledResolution = mc.scaledResolution;
            final int width = scaledResolution.getScaledWidth();
            final int height = scaledResolution.getScaledHeight();

            boolean shouldRender = mc.currentScreen instanceof GuiChat;

            if (!shouldRender) {
                selectedValue = null;
            } else {
                closeStopWatch.reset();
            }

            animationAlpha.setEasing(LINEAR);
            animationAlpha.setDuration(300);
            animationAlpha.run(shouldRender ? 100 : 0);

            if (animationAlpha.getValue() <= 0 && closeStopWatch.finished(0)) {
                selectedValue = null;
                return;
            }

            modules.clear();
            Sakura.instance.getModuleManager().getAll().stream().filter(module ->
                            module.isEnabled() && module.getValues().stream().
                                    anyMatch(value -> value instanceof DragValue)).
                    forEach(modules::add);

            if (selectedValue != null) {
                Vector2d mouse = MouseUtil.mouse();
                final double positionX = mouse.x + offset.x;
                final double positionY = mouse.y + offset.y;

                selectedValue.targetPosition = new Vector2d(positionX, positionY);

                // Setup snapping
                snaps.clear();

                double edgeSnap = Sakura.instance.getThemeManager().getTheme().getPadding();

                // Permanent snaps
                snaps.add(new Snap(width / 2f, 5, Orientation.HORIZONTAL, true, true, true));
                snaps.add(new Snap(height / 2f, 5, Orientation.VERTICAL, true, true, true));

                snaps.add(new Snap(height - edgeSnap, 5, Orientation.VERTICAL, false, false, true));
                snaps.add(new Snap(edgeSnap, 5, Orientation.VERTICAL, false, true, false));
                snaps.add(new Snap(width - edgeSnap, 5, Orientation.HORIZONTAL, false, false, true));
                snaps.add(new Snap(edgeSnap, 5, Orientation.HORIZONTAL, false, true, false));

                for (Module module : modules) {
                    // Getting Position Value
                    Optional<Value<?>> positionValues = module.getValues().stream().filter(value ->
                            value instanceof DragValue).findFirst();
                    DragValue positionValue = ((DragValue) positionValues.get());

                    if (positionValue == selectedValue) continue;

                    snaps.add(new Snap(positionValue.position.x + positionValue.scale.x + edgeSnap, 5, Orientation.HORIZONTAL, false, true, false));
                    snaps.add(new Snap(positionValue.position.x - edgeSnap, 5, Orientation.HORIZONTAL, false, false, true));

                    snaps.add(new Snap(positionValue.position.y, 5, Orientation.VERTICAL, false, false, true));
                    snaps.add(new Snap(positionValue.position.y + positionValue.scale.y, 5, Orientation.VERTICAL, false, true, false));
                }

                double closest;
                selected = null;
                Color color = ColorUtil.withAlpha(Color.WHITE, 60);

                for (Snap snap : snaps) {
                    switch (snap.orientation) {
                        case VERTICAL:
                            closest = Double.MAX_VALUE;

                            for (double y = -selectedValue.scale.y; y <= 0; y += selectedValue.scale.y / 2f) {
                                if ((y == -selectedValue.scale.y / 2 && !snap.center) || (y == -selectedValue.scale.y && !snap.left) || (y == 0 && !snap.right)) {
                                    continue;
                                }

                                double distance = Math.abs(selectedValue.targetPosition.y - (snap.position + y));

                                if (distance < snap.distance && distance < closest) {
                                    closest = distance;
                                    selectedValue.targetPosition.y = snap.position + y;
                                    selected = snap;
                                    RenderUtil.rectangle(0, selected.position, scaledResolution.getScaledWidth(), 0.5, color);
                                }
                            }
                            break;

                        case HORIZONTAL:
                            closest = Double.MAX_VALUE;
                            for (double x = -selectedValue.scale.x; x <= 0; x += selectedValue.scale.x / 2f) {
                                if ((x == -selectedValue.scale.x / 2 && !snap.center) || (x == -selectedValue.scale.x && !snap.left) || (x == 0 && !snap.right)) {
                                    continue;
                                }

                                double distance = Math.abs(selectedValue.targetPosition.x - (snap.position + x));

                                if (distance < snap.distance && distance < closest) {
                                    closest = distance;
                                    selectedValue.targetPosition.x = snap.position + x;
                                    selected = snap;
                                    RenderUtil.rectangle(selected.position, 0, 0.5, scaledResolution.getScaledHeight(), color);
                                }
                            }
                            break;
                    }
                }
            }

            // Validating position
            for (Module module : modules) {
                // Getting Position Value
                Optional<Value<?>> positionValues = module.getValues().stream().filter(value ->
                        value instanceof DragValue).findFirst();
                DragValue positionValue = ((DragValue) positionValues.get());

                float offset = Sakura.instance.getThemeManager().getTheme().getPadding();

                positionValue.position.x = Math.max(offset, positionValue.position.x);
                positionValue.position.x = Math.min(width - positionValue.scale.x - offset, positionValue.position.x);

                positionValue.position.y = Math.max(offset, positionValue.position.y);
                positionValue.position.y = Math.min(height - positionValue.scale.y - offset, positionValue.position.y);

                positionValue.targetPosition.x = Math.max(offset, positionValue.targetPosition.x);
                positionValue.targetPosition.x = Math.min(width - positionValue.scale.x - offset, positionValue.targetPosition.x);

                positionValue.targetPosition.y = Math.max(offset, positionValue.targetPosition.y);
                positionValue.targetPosition.y = Math.min(height - positionValue.scale.y - offset, positionValue.targetPosition.y);

                positionValue.position = new Vector2d(Math.min(width - positionValue.scale.x - offset, positionValue.targetPosition.x), Math.min(height - positionValue.scale.y - offset, positionValue.targetPosition.y));
            }
            stopWatch.reset();
        } catch (Exception exception) {
            exception.printStackTrace();
            //System.out.println("exception");
        }
    };

    @EventLink
    public final Listener<GuiClickEvent> onGuiClick = event -> {
        if (event.getMouseButton() != 0) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            for (final Module module : modules) {
                for (final Value<?> value : module.getValues()) {
                    if (value instanceof DragValue) {
                        final DragValue positionValue = (DragValue) value;
                        final Vector2d position = positionValue.position;
                        final Vector2d scale = positionValue.scale;
                        final float mouseX = event.getMouseX();
                        final float mouseY = event.getMouseY();

                        if (!positionValue.structure && GUIUtil.mouseOver(position, scale, mouseX, mouseY)) {
                            selectedValue = positionValue;

                            offset = new Vector2d(position.x - mouseX, position.y - mouseY);
                        }
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<GuiMouseReleaseEvent> onGuiMouseRelease = event -> {
        selectedValue = null;
    };
}