package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeWispHarvestLevelData extends CAbilityTypeLevelData {
	private final int lumberPerInterval;
	private final float artAttachmentHeight;
	private final float castRange;
	private final float duration;

	public CAbilityTypeWispHarvestLevelData(final EnumSet<CTargetType> targetsAllowed, final int lumberPerInterval,
			final float artAttachmentHeight, final float castRange, final float duration) {
		super(targetsAllowed);
		this.lumberPerInterval = lumberPerInterval;
		this.artAttachmentHeight = artAttachmentHeight;
		this.castRange = castRange;
		this.duration = duration;
	}

	public int getLumberPerInterval() {
		return this.lumberPerInterval;
	}

	public float getArtAttachmentHeight() {
		return this.artAttachmentHeight;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getDuration() {
		return this.duration;
	}

}
