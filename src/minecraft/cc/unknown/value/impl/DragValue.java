package cc.unknown.value.impl;

import static cc.unknown.util.animation.Easing.EASE_OUT_EXPO;
import static cc.unknown.util.animation.Easing.LINEAR;

import java.util.List;
import java.util.function.BooleanSupplier;

import cc.unknown.module.Module;
import cc.unknown.util.Accessor;
import cc.unknown.util.animation.Animation;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.value.Mode;
import cc.unknown.value.Value;
import net.minecraft.client.gui.ScaledResolution;

public class DragValue extends Value<Vector2d> implements Accessor {

    public Vector2d position = new Vector2d(100, 100), targetPosition = new Vector2d(100, 100), scale = new Vector2d(100, 100), lastScale = new Vector2d(-1, -1);
    public Animation animationPosition = new Animation(LINEAR, 600), smoothAnimation = new Animation(EASE_OUT_EXPO, 300);
    public ScaledResolution lastScaledResolution = new ScaledResolution(mc);
    public boolean render = true, structure;

    public DragValue(final String name, final Module parent, final Vector2d defaultValue) {
        super(name, parent, defaultValue);
    }

    public DragValue(final String name, final Module parent, final Vector2d defaultValue, final boolean render) {
        super(name, parent, defaultValue);
        this.render = render;
    }

    public DragValue(final String name, final Module parent, final Vector2d defaultValue, final boolean render, final boolean structure) {
        super(name, parent, defaultValue);
        this.render = render && !structure;
        this.structure = structure;
    }

    public DragValue(final String name, final Mode<?> parent, final Vector2d defaultValue) {
        super(name, parent, defaultValue);
    }

    public DragValue(final String name, final Module parent, final Vector2d defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    public DragValue(final String name, final Mode<?> parent, final Vector2d defaultValue, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public void setScale(Vector2d scale) {
        this.scale = scale;
        if (lastScale.x == -1 && lastScale.y == -1) {
            this.lastScale = this.scale;
        }

        ScaledResolution scaledResolution = mc.scaledResolution;

        if (this.position.x > scaledResolution.getScaledWidth() / 2f) {
            this.targetPosition.x += this.lastScale.x - this.scale.x;
            this.position = targetPosition;
        }

        if (this.position.y > scaledResolution.getScaledHeight() / 2f) {
            this.targetPosition.y += this.lastScale.y - this.scale.y;
            this.position = targetPosition;
        }

        this.lastScale = scale;
        this.lastScaledResolution = scaledResolution;
    }

	public Vector2d getPosition() {
		return position;
	}

	public Vector2d getTargetPosition() {
		return targetPosition;
	}

	public Vector2d getScale() {
		return scale;
	}

	public Vector2d getLastScale() {
		return lastScale;
	}

	public Animation getAnimationPosition() {
		return animationPosition;
	}

	public Animation getSmoothAnimation() {
		return smoothAnimation;
	}

	public ScaledResolution getLastScaledResolution() {
		return lastScaledResolution;
	}

	public boolean isRender() {
		return render;
	}

	public boolean isStructure() {
		return structure;
	}

	public void setPosition(Vector2d position) {
		this.position = position;
	}

	public void setTargetPosition(Vector2d targetPosition) {
		this.targetPosition = targetPosition;
	}

	public void setLastScale(Vector2d lastScale) {
		this.lastScale = lastScale;
	}

	public void setAnimationPosition(Animation animationPosition) {
		this.animationPosition = animationPosition;
	}

	public void setSmoothAnimation(Animation smoothAnimation) {
		this.smoothAnimation = smoothAnimation;
	}

	public void setLastScaledResolution(ScaledResolution lastScaledResolution) {
		this.lastScaledResolution = lastScaledResolution;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public void setStructure(boolean structure) {
		this.structure = structure;
	}
}