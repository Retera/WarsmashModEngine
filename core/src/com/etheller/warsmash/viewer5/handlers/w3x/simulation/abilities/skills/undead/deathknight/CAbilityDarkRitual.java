package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityDarkRitual extends CAbilityDeathPact {

	public CAbilityDarkRitual(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.darkritual;
	}
}
