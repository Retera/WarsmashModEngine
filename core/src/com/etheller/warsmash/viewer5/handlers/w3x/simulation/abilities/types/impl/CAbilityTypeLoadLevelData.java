package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeLoadLevelData extends CAbilityTypeLevelData {
	private final float castRange;

	public CAbilityTypeLoadLevelData(final EnumSet<CTargetType> targetsAllowed, final float castRange) {
		super(targetsAllowed);
		this.castRange = castRange;
	}

	public float getCastRange() {
		return this.castRange;
	}

}
