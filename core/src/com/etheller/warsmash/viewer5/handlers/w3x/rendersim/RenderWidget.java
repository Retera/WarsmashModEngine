package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.GenericNode;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxNode;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitAnimationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

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

	boolean isSelectable(CSimulation simulation, int byPlayer);

	boolean isShowSelectionCircleAboveWater();

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
		private final MdxNode turretBone;
		private LockTarget turretFacingLock;
		private final MdxNode headBone;
		private LockTarget headFacingLock;

		public UnitAnimationListenerImpl(final MdxComplexInstance instance, final float animationWalkSpeed,
				final float animationRunSpeed) {
			this.instance = instance;
			this.animationWalkSpeed = animationWalkSpeed;
			this.animationRunSpeed = animationRunSpeed;
			this.turretBone = this.instance.inefficientlyGetNodeByNameSearch("bone_turret");
			this.headBone = this.instance.inefficientlyGetNodeByNameSearch("bone_head");
		}

		@Override
		public void playWalkAnimation(final boolean force, final float currentMovementSpeed,
				final boolean allowRarityVariations) {
			EnumSet<SecondaryTag> secondaryWalkTags;
			float animationMoveSpeed;
			if (this.animationWalkSpeed < this.animationRunSpeed) {
				final float midpoint = (this.animationWalkSpeed + this.animationRunSpeed) / 2;
				if (currentMovementSpeed >= midpoint) {
					secondaryWalkTags = SequenceUtils.FAST;
					animationMoveSpeed = this.animationRunSpeed;
				}
				else {
					secondaryWalkTags = SequenceUtils.EMPTY;
					animationMoveSpeed = this.animationWalkSpeed;
				}
			}
			else {
				secondaryWalkTags = SequenceUtils.EMPTY;
				animationMoveSpeed = this.animationWalkSpeed;
			}
			animationMoveSpeed *= this.instance.localScale.x;
			final float speedRatio = (currentMovementSpeed) / animationMoveSpeed;
			playAnimation(force, PrimaryTag.WALK, secondaryWalkTags, speedRatio, allowRarityVariations);
		}

		@Override
		public boolean addSecondaryTag(final AnimationTokens.SecondaryTag tag) {
			return this.secondaryAnimationTags.add(tag);
		}

		@Override
		public boolean removeSecondaryTag(final AnimationTokens.SecondaryTag tag) {
			return this.secondaryAnimationTags.remove(tag);
		}

		@Override
		public void forceResetCurrentAnimation() {
			if (!this.animationQueue.isEmpty()) {
				final QueuedAnimation nextAnimation = this.animationQueue.poll();
				playAnimation(true, nextAnimation.animationName, nextAnimation.secondaryAnimationTags, 1.0f,
						nextAnimation.allowRarityVariations);
			}
			else {
				playAnimation(true, this.currentAnimation, this.currentAnimationSecondaryTags, this.currentSpeedRatio,
						this.currentlyAllowingRarityVariations);
			}
		}

		@Override
		public EnumSet<SecondaryTag> getSecondaryTags() {
			return this.secondaryAnimationTags;
		}

		@Override
		public void playAnimation(final boolean force, final int sequenceIndex, final float speedRatio,
				final boolean allowRarityVariations) {
			this.animationQueue.clear();
			if (force || this.instance.sequenceEnded) {
				this.currentSpeedRatio = speedRatio;
				this.instance.setAnimationSpeed(speedRatio);
				final MdxModel model = (MdxModel) this.instance.model;
				if ((sequenceIndex >= 0) && (sequenceIndex < model.sequences.size())) {
					final Sequence sequence = model.sequences.get(sequenceIndex);
					final boolean isWalk = sequence.getPrimaryTags().contains(PrimaryTag.WALK);
					if (!isWalk && (this.currentAnimation == PrimaryTag.WALK)) {
						this.lastWalkFrame = this.instance.frame;
					}
					this.instance.setSequence(sequenceIndex);
					if ((this.lastWalkFrame != -1) && (isWalk) && (this.currentAnimation != PrimaryTag.WALK)) {
						this.instance.setFrame(this.instance.clampFrame(this.lastWalkFrame));
					}
					this.currentAnimation = Sequence.any(sequence.getPrimaryTags());
					this.currentAnimationSecondaryTags = sequence.getSecondaryTags();
					this.currentlyAllowingRarityVariations = allowRarityVariations;
				}
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
				if ((animationName != PrimaryTag.WALK) && (this.currentAnimation == PrimaryTag.WALK)) {
					this.lastWalkFrame = this.instance.frame;
				}
				if (SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations) != null) {
					if ((this.lastWalkFrame != -1) && (animationName == PrimaryTag.WALK)
							&& (this.currentAnimation != PrimaryTag.WALK)) {
						this.instance.setFrame(this.instance.clampFrame(this.lastWalkFrame));
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
				if ((animationName != PrimaryTag.WALK) && (this.currentAnimation == PrimaryTag.WALK)) {
					this.lastWalkFrame = this.instance.frame;
				}
				final Sequence sequence = SequenceUtils.randomSequence(this.instance, animationName, this.recycleSet,
						allowRarityVariations);
				if (sequence != null) {
					if ((this.lastWalkFrame != -1) && (animationName == PrimaryTag.WALK)
							&& (this.currentAnimation != PrimaryTag.WALK)) {
						this.instance.setFrame(this.instance.clampFrame(this.lastWalkFrame));
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
			applyLock(this.turretBone, this.turretFacingLock);
			applyLock(this.headBone, this.headFacingLock);
		}

		private static void applyLockAbilityTarget(final MdxNode turretBone, final AbilityTarget turretFacingLock) {
			if (turretBone != null) {
				if (turretFacingLock == null) {
					if (turretBone.overrideWorldRotation != null) {
						turretBone.setOverrideWorldRotation(null);
					}
				}
				else {
					final float ang = (float) Math.atan2(turretFacingLock.getY() - turretBone.worldLocation.y,
							turretFacingLock.getX() - turretBone.worldLocation.x);
					if (turretBone.overrideWorldRotation == null) {
						turretBone.setOverrideWorldRotation(new Quaternion());
					}
					turretBone.overrideWorldRotation.setFromAxisRad(0, 0, 1, ang);
				}
			}
		}

		private static void applyLockGenericNode(final MdxNode turretBone, final GenericNode turretFacingLock) {
			if (turretBone != null) {
				if (turretFacingLock == null) {
					if (turretBone.overrideWorldRotation != null) {
						turretBone.setOverrideWorldRotation(null);
					}
				}
				else {
					final float dx = turretFacingLock.worldLocation.x - turretBone.worldLocation.x;
					final float dy = turretFacingLock.worldLocation.y - turretBone.worldLocation.y;
					final float dz = turretFacingLock.worldLocation.z - turretBone.worldLocation.z;
					final float ang = (float) Math.atan2(dy, dx);
					final double groundDistance = Math.sqrt((dx * dx) + (dy * dy));
					final float angZ = (float) Math.atan2(dz, groundDistance);
					if (turretBone.overrideWorldRotation == null) {
						turretBone.setOverrideWorldRotation(new Quaternion());
					}
					turretBone.overrideWorldRotation.setFromAxisRad(0, 0, 1, ang);
					final float x = turretBone.overrideWorldRotation.x;
					final float y = turretBone.overrideWorldRotation.y;
					final float z = turretBone.overrideWorldRotation.z;
					final float w = turretBone.overrideWorldRotation.w;
					turretBone.overrideWorldRotation.setFromAxisRad(0, 1, 0, angZ);
					turretBone.overrideWorldRotation.mulLeft(x, y, z, w);
				}
			}
		}

		private static void applyLock(final MdxNode turretBone, final LockTarget turretFacingLock) {
			if (turretBone != null) {
				if (turretFacingLock == null) {
					if (turretBone.overrideWorldRotation != null) {
						turretBone.setOverrideWorldRotation(null);
					}
				}
				else {
					if (turretBone.overrideWorldRotation == null) {
						turretBone.setOverrideWorldRotation(new Quaternion());
					}
					turretFacingLock.apply(turretBone, turretBone.overrideWorldRotation);
				}
			}
		}

		@Override
		public void lockTurretFacing(final AbilityTarget target) {
			this.turretFacingLock = new LockTargetGame(target);
		}

		public void lockTurretFacing(final GenericNode modelComponent, final Vector3 offset) {
			this.turretFacingLock = new LockTargetRenderGeometry(modelComponent, offset);
		}

		@Override
		public void clearTurretFacing() {
			this.turretFacingLock = null;
		}

		@Override
		public void lockHeadFacing(final AbilityTarget target) {
			this.headFacingLock = new LockTargetGame(target);
		}

		public void lockHeadFacing(final GenericNode modelComponent, final Vector3 offset) {
			this.headFacingLock = new LockTargetRenderGeometry(modelComponent, offset);
		}

		@Override
		public void clearHeadFacing() {
			this.headFacingLock = null;
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
