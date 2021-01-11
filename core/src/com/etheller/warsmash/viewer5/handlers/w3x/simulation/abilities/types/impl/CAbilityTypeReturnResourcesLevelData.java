package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeReturnResourcesLevelData extends CAbilityTypeLevelData {

	private final boolean acceptsGold;
	private final boolean acceptsLumber;

	public CAbilityTypeReturnResourcesLevelData(final EnumSet<CTargetType> targetsAllowed, final boolean acceptsGold,
			final boolean acceptsLumber) {
		super(targetsAllowed);
		this.acceptsGold = acceptsGold;
		this.acceptsLumber = acceptsLumber;
	}

	public boolean isAcceptsGold() {
		return this.acceptsGold;
	}

	public boolean isAcceptsLumber() {
		return this.acceptsLumber;
	}

}
