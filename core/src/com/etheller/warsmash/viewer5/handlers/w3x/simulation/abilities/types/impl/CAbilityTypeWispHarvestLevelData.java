package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public class CAbilityTypeWispHarvestLevelData extends CAbilityTypeLevelData {
	private final int lumberPerInterval;
	private final float artAttachmentHeight;
	private final float castRange;
	private final float duration;

	public CAbilityTypeWispHarvestLevelData(final EnumSet<CTargetType> targetsAllowed, final int lumberPerInterval,
                                            float artAttachmentHeight, final float castRange, final float duration) {
		super(targetsAllowed);
		this.lumberPerInterval = lumberPerInterval;
		this.artAttachmentHeight = artAttachmentHeight;
		this.castRange = castRange;
		this.duration = duration;
	}

	public int getLumberPerInterval() {
		return lumberPerInterval;
	}

	public float getArtAttachmentHeight() {
		return artAttachmentHeight;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getDuration() {
		return this.duration;
	}

}
