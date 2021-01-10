package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeLevelData {
	private final EnumSet<CTargetType> targetsAllowed;

	public CAbilityTypeLevelData(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}
}
