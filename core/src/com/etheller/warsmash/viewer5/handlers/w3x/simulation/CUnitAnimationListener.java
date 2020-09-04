package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;

public interface CUnitAnimationListener {
	EnumSet<SecondaryTag> EMPTY = EnumSet.noneOf(SecondaryTag.class);
	EnumSet<SecondaryTag> READY = EnumSet.of(SecondaryTag.READY);

	void playAnimation(boolean force, final PrimaryTag animationName,
			final EnumSet<SecondaryTag> secondaryAnimationTags, float speedRatio);

	void queueAnimation(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryAnimationTags);
}
