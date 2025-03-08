package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public interface CUnitAnimationListener {
	void playAnimation(boolean force, final PrimaryTag animationName,
			final EnumSet<SecondaryTag> secondaryAnimationTags, float speedRatio, boolean allowRarityVariations);

	void playAnimationWithDuration(final boolean force, final PrimaryTag animationName,
			final EnumSet<SecondaryTag> secondaryAnimationTags, final float duration,
			final boolean allowRarityVariations);

	void playWalkAnimation(boolean force, float currentMovementSpeed, boolean allowRarityVariations);

	void playAnimation(boolean force, int sequenceIndex, float speedRatio, boolean allowRarityVariations);

	void queueAnimation(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryAnimationTags,
			boolean allowRarityVariations);

	boolean addSecondaryTag(SecondaryTag secondaryTag);

	boolean removeSecondaryTag(SecondaryTag secondaryTag);

	void forceResetCurrentAnimation();

	EnumSet<SecondaryTag> getSecondaryTags();

	void lockTurretFacing(AbilityTarget target);

	void clearTurretFacing();

	void lockHeadFacing(AbilityTarget target);

	void clearHeadFacing();

}
