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

	float getZ();

	void unassignSelectionCircle();

	void assignSelectionCircle(SplatMover t);

	void unassignSelectionPreviewHighlight();

	void assignSelectionPreviewHighlight(SplatMover t);

	SplatMover getSelectionCircle();

	boolean isSelectable();

	public static final class UnitAnimationListenerImpl implements CUnitAnimationListener {
		private final MdxComplexInstance instance;
		protected final EnumSet<AnimationTokens.SecondaryTag> secondaryAnimationTags = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private final EnumSet<AnimationTokens.SecondaryTag> recycleWalkFastSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private PrimaryTag currentAnimation;
		private EnumSet<SecondaryTag> currentAnimationSecondaryTags = SequenceUtils.EMPTY;
		private float currentSpeedRatio;
		private boolean currentlyAllowingRarityVariations;
		private final Queue<QueuedAnimation> animationQueue = new LinkedList<>();
		private int lastWalkFrame = -1;
		private final float animationWalkSpeed;
		private final float animationRunSpeed;

		public UnitAnimationListenerImpl(final MdxComplexInstance instance, final float animationWalkSpeed,
				final float animationRunSpeed) {
			this.instance = instance;
			this.animationWalkSpeed = animationWalkSpeed;
			this.animationRunSpeed = animationRunSpeed;
		}

		@Override
		public void playWalkAnimation(final boolean force, final float currentMovementSpeed,
				final boolean allowRarityVariations) {
			EnumSet<SecondaryTag> secondaryWalkTags;
			float animationMoveSpeed;
			if (animationWalkSpeed < animationRunSpeed) {
				final float midpoint = (animationWalkSpeed + animationRunSpeed) / 2;
				if (currentMovementSpeed >= midpoint) {
					secondaryWalkTags = SequenceUtils.FAST;
					animationMoveSpeed = animationRunSpeed;
				}
				else {
					secondaryWalkTags = SequenceUtils.EMPTY;
					animationMoveSpeed = animationWalkSpeed;
				}
			}
			else {
				secondaryWalkTags = SequenceUtils.EMPTY;
				animationMoveSpeed = animationWalkSpeed;
			}
			animationMoveSpeed *= instance.localScale.x;
			final float speedRatio = (currentMovementSpeed) / animationMoveSpeed;
			playAnimation(force, PrimaryTag.WALK, secondaryWalkTags, speedRatio, allowRarityVariations);
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
					|| !secondaryAnimationTags.equals(this.currentAnimationSecondaryTags)
					|| this.instance.sequenceEnded) {
				this.currentSpeedRatio = speedRatio;
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryAnimationTags);
				this.recycleSet.addAll(secondaryAnimationTags);
				this.instance.setAnimationSpeed(speedRatio);
				if ((animationName != PrimaryTag.WALK) && (currentAnimation == PrimaryTag.WALK)) {
					lastWalkFrame = instance.frame;
				}
				if (SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations) != null) {
					if ((lastWalkFrame != -1) && (animationName == PrimaryTag.WALK)
							&& (currentAnimation != PrimaryTag.WALK)) {
						instance.setFrame(instance.clampFrame(lastWalkFrame));
					}
					this.currentAnimation = animationName;
					this.currentAnimationSecondaryTags = secondaryAnimationTags;
					this.currentlyAllowingRarityVariations = allowRarityVariations;
				}
			}
		}

		@Override
		public void playAnimationWithDuration(final boolean force, final PrimaryTag animationName,
				final EnumSet<SecondaryTag> secondaryAnimationTags, final float duration,
				final boolean allowRarityVariations) {
			this.animationQueue.clear();
			if (force || (animationName != this.currentAnimation)
					|| !secondaryAnimationTags.equals(this.currentAnimationSecondaryTags)) {
				this.recycleSet.clear();
				this.recycleSet.addAll(this.secondaryAnimationTags);
				this.recycleSet.addAll(secondaryAnimationTags);
				if ((animationName != PrimaryTag.WALK) && (currentAnimation == PrimaryTag.WALK)) {
					lastWalkFrame = instance.frame;
				}
				final Sequence sequence = SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations);
				if (sequence != null) {
					if ((lastWalkFrame != -1) && (animationName == PrimaryTag.WALK)
							&& (currentAnimation != PrimaryTag.WALK)) {
						instance.setFrame(instance.clampFrame(lastWalkFrame));
					}
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

	SplatMover getSelectionPreviewHighlight();
}
