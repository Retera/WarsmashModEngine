package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public class CAbilityTypeItemReincarnationLevelData extends CAbilityTypeLevelData {

	private final int delay;
	private final int restoredLife;
	private final int restoredMana;

	public CAbilityTypeItemReincarnationLevelData(final EnumSet<CTargetType> targetsAllowed, final int delay,
                                                  final int restoredLife, final int restoredMana) {
		super(targetsAllowed);
		this.delay = delay;
		this.restoredLife = restoredLife;
		this.restoredMana = restoredMana;
	}

	public int getDelay() {
		return this.delay;
	}

	public int getRestoredLife() {
		return this.restoredLife;
	}

	public int getRestoredMana() {
		return this.restoredMana;
	}
}
