package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;

public class CAbilityData {
	private static final War3ID COLD_ARROWS = War3ID.fromString("ACcw");
	private final MutableObjectData abilityData;

	public CAbilityData(final MutableObjectData abilityData) {
		this.abilityData = abilityData;
	}

	public CAbility createAbility(final String ability, final int handleId) {
		final War3ID war3Id = War3ID.fromString(ability);
		if (war3Id.equals(COLD_ARROWS)) {
			return new CAbilityColdArrows(war3Id, handleId);
		}
		return new CAbilityGeneric(war3Id, handleId);
	}
}
