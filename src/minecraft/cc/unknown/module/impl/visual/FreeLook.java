package cc.unknown.module.impl.visual;

import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TeleportEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.value.impl.BooleanValue;

@ModuleInfo(aliases = {"Free Look"}, description = "Allows you to look around you without changing your direction", category = Category.VISUALS)
public final class FreeLook extends Module {

    private int previousPerspective;
    public float originalYaw, originalPitch, lastYaw, lastPitch;

    @Override
    public void onEnable() {
        previousPerspective = mc.gameSettings.thirdPersonView;
        originalYaw = lastYaw = mc.player.rotationYaw;
        originalPitch = lastPitch = mc.player.rotationPitch;

        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void onDisable() {
        mc.player.rotationYaw = originalYaw;
        mc.player.rotationPitch = originalPitch;
        mc.gameSettings.thirdPersonView = previousPerspective;
    }

    @EventLink(value = Priority.LOW)
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (this.getKey() == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.getKey())) {
            this.setEnabled(false);
            return;
        }

        this.mc.mouseHelper.mouseXYChange();
        final float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f1 = (float) (f * f * f * 1.5);
        lastYaw += this.mc.mouseHelper.deltaX * f1;
        lastPitch -= this.mc.mouseHelper.deltaY * f1;

        lastPitch = MathHelper.clamp_float(lastPitch, -90, 90);
        mc.gameSettings.thirdPersonView = 1;
    };

    @EventLink(value = Priority.LOW)
    public final Listener<TeleportEvent> onTeleport = event -> {
        originalYaw = event.getYaw();
        originalPitch = event.getPitch();
    };
}