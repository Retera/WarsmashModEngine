package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitAnimationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public interface RenderWidget {
	MdxComplexInstance getInstance();

	CWidget getSimulationWidget();

	void updateAnimations(War3MapViewer war3MapViewer);

	boolean isIntersectedOnMeshAlways();

	float getSelectionScale();

	float getX();

	float getY();

	void unassignSelectionCircle();

	void assignSelectionCircle(SplatMover t);

	public static final class UnitAnimationListenerImpl implements CUnitAnimationListener {
		private final MdxComplexInstance instance;
		protected final EnumSet<AnimationTokens.SecondaryTag> secondaryAnimationTags = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private PrimaryTag currentAnimation;
		private EnumSet<SecondaryTag> currentAnimationSecondaryTags;
		private float currentSpeedRatio;
		private boolean currentlyAllowingRarityVariations;
		private final Queue<QueuedAnimation> animationQueue = new LinkedList<>();

		public UnitAnimationListenerImpl(final MdxComplexInstance instance) {
			this.instance = instance;
		}

		@Override
		public void addSecondaryTag(final AnimationTokens.SecondaryTag tag) {
			if (!secondaryAnimationTags.contains(tag)) {
				this.secondaryAnimationTags.add(tag);
				if (!animationQueue.isEmpty()) {
					final QueuedAnimation nextAnimation = animationQueue.poll();
					playAnimation(true, nextAnimation.animationName, nextAnimation.secondaryAnimationTags, 1.0f,
							nextAnimation.allowRarityVariations);
				}
				else {
					playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags,
							this.currentSpeedRatio, this.currentlyAllowingRarityVariations);
				}
			}
		}

		@Override
		public void removeSecondaryTag(final AnimationTokens.SecondaryTag tag) {
			if (secondaryAnimationTags.contains(tag)) {
				this.secondaryAnimationTags.remove(tag);
				playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags, this.currentSpeedRatio,
						this.currentlyAllowingRarityVariations);
			}
		}

		@Override
		public void playAnimation(final boolean force, final PrimaryTag animationName,
				final EnumSet<SecondaryTag> secondaryAnimationTags, final float speedRatio,
				final boolean allowRarityVariations) {
			this.animationQueue.clear();
			if (force || (animationName != this.currentAnimation)
					|| !secondaryAnimationTags.equals(this.currentAnimationSecondaryTags)) {
				this.currentSpeedRatio = speedRatio;
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryAnimationTags);
				this.recycleSet.addAll(secondaryAnimationTags);
				this.instance.setAnimationSpeed(speedRatio);
				if (SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations) != null) {
					this.currentAnimation = animationName;
					this.currentAnimationSecondaryTags = secondaryAnimationTags;
					this.currentlyAllowingRarityVariations = allowRarityVariations;
				}
			}
		}

		public void playAnimationWithDuration(final boolean force, final PrimaryTag animationName,
				final EnumSet<SecondaryTag> secondaryAnimationTags, final float duration,
				final boolean allowRarityVariations) {
			this.animationQueue.clear();
			if (force || (animationName != this.currentAnimation)
					|| !secondaryAnimationTags.equals(this.currentAnimationSecondaryTags)) {
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryAnimationTags);
				this.recycleSet.addAll(secondaryAnimationTags);
				final Sequence sequence = SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations);
				if (sequence != null) {
					this.currentAnimation = animationName;
					this.currentAnimationSecondaryTags = secondaryAnimationTags;
					this.currentlyAllowingRarityVariations = allowRarityVariations;
					this.currentSpeedRatio = ((sequence.getInterval()[1] - sequence.getInterval()[0]) / 1000.0f)
							/ duration;
					this.instance.setAnimationSpeed(this.currentSpeedRatio);
				}
			}
		}

		@Override
		public void queueAnimation(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryAnimationTags,
				final boolean allowRarityVariations) {
			this.animationQueue.add(new QueuedAnimation(animationName, secondaryAnimationTags, allowRarityVariations));
		}

		public void update() {
			if (this.instance.sequenceEnded || (this.instance.sequence == -1)) {
				// animation done
				if ((this.instance.sequence != -1) && (((MdxModel) this.instance.model).getSequences()
						.get(this.instance.sequence).getFlags() == 0)) {
					// animation is a looping animation
					playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags,
							this.currentSpeedRatio, this.currentlyAllowingRarityVariations);
				}
				else {
					final QueuedAnimation nextAnimation = this.animationQueue.poll();
					if (nextAnimation != null) {
						playAnimation(true, nextAnimation.animationName, nextAnimation.secondaryAnimationTags, 1.0f,
								nextAnimation.allowRarityVariations);
					}
				}
			}
		}
	}

	public static final class QueuedAnimation {
		private final PrimaryTag animationName;
		private final EnumSet<SecondaryTag> secondaryAnimationTags;
		private final boolean allowRarityVariations;

		public QueuedAnimation(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryAnimationTags,
				final boolean allowRarityVariations) {
			this.animationName = animationName;
			this.secondaryAnimationTags = secondaryAnimationTags;
			this.allowRarityVariations = allowRarityVariations;
		}
	}
}
