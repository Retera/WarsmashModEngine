package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class ABCallbackGetUnitDefenseType extends ABDefenseTypeCallback {

	private ABUnitCallback unit;

	@Override
	public CDefenseType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return unit.callback(game, caster, localStore, castId).getDefenseType();
	}

}
