package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeItemStatBonusLevelData extends CAbilityTypeLevelData {

	private final int strength;
	private final int agility;
	private final int intelligence;

	public CAbilityTypeItemStatBonusLevelData(final EnumSet<CTargetType> targetsAllowed, final int strength,
			final int agility, final int intelligence) {
		super(targetsAllowed);
		this.strength = strength;
		this.agility = agility;
		this.intelligence = intelligence;
	}

	public int getStrength() {
		return this.strength;
	}

	public int getAgility() {
		return this.agility;
	}

	public int getIntelligence() {
		return this.intelligence;
	}
}
