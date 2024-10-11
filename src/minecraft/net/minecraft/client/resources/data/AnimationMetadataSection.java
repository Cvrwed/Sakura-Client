package net.minecraft.client.resources.data;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AnimationMetadataSection implements IMetadataSection {
	private final List<AnimationFrame> animationFrames;
	private final int frameWidth;
	private final int frameHeight;
	private final int frameTime;
	private final boolean interpolate;

	public int getFrameCount() {
		return this.animationFrames.size();
	}

	private AnimationFrame getAnimationFrame(int p_130072_1_) {
		return (AnimationFrame) this.animationFrames.get(p_130072_1_);
	}

	public int getFrameTimeSingle(int p_110472_1_) {
		AnimationFrame animationframe = this.getAnimationFrame(p_110472_1_);
		return animationframe.hasNoTime() ? this.frameTime : animationframe.getFrameTime();
	}

	public boolean frameHasTime(int p_110470_1_) {
		return !((AnimationFrame) this.animationFrames.get(p_110470_1_)).hasNoTime();
	}

	public int getFrameIndex(int p_110468_1_) {
		return ((AnimationFrame) this.animationFrames.get(p_110468_1_)).getFrameIndex();
	}

	public Set<Integer> getFrameIndexSet() {
		Set<Integer> set = Sets.<Integer>newHashSet();

		for (AnimationFrame animationframe : this.animationFrames) {
			set.add(Integer.valueOf(animationframe.getFrameIndex()));
		}

		return set;
	}
}
