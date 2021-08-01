package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeItemAttackBonusLevelData extends CAbilityTypeLevelData {
	private final int damageBonus;

	public CAbilityTypeItemAttackBonusLevelData(final EnumSet<CTargetType> targetsAllowed, final int damageBonus) {
		super(targetsAllowed);
		this.damageBonus = damageBonus;
	}

	public int getDamageBonus() {
		return this.damageBonus;
	}
}
