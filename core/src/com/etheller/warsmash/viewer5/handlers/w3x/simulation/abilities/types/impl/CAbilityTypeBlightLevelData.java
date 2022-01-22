package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeBlightLevelData extends CAbilityTypeLevelData {

	private final boolean createsBlight;
	private final float expansionAmount;
	private final float areaOfEffect;
	private final float gameSecondsPerBlightExpansion;

	public CAbilityTypeBlightLevelData(final EnumSet<CTargetType> targetsAllowed, final boolean createsBlight,
			final float expansionAmount, final float areaOfEffect, final float gameSecondsPerBlightExpansion) {
		super(targetsAllowed);
		this.createsBlight = createsBlight;
		this.expansionAmount = expansionAmount;
		this.areaOfEffect = areaOfEffect;
		this.gameSecondsPerBlightExpansion = gameSecondsPerBlightExpansion;
	}

	public boolean isCreatesBlight() {
		return this.createsBlight;
	}

	public float getExpansionAmount() {
		return this.expansionAmount;
	}

	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	public float getGameSecondsPerBlightExpansion() {
		return this.gameSecondsPerBlightExpansion;
	}

}
