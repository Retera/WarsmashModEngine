package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeCargoHoldBurrowLevelData extends CAbilityTypeCargoHoldLevelData {

	public CAbilityTypeCargoHoldBurrowLevelData(EnumSet<CTargetType> targetsAllowed, int cargoCapcity, float duration,
			float castRange) {
		super(targetsAllowed, cargoCapcity, duration, castRange);
	}

}
