package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class ABCallbackGetDefenseTypeFromString extends ABDefenseTypeCallback {

	private ABStringCallback id;

	@Override
	public CDefenseType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return CDefenseType.valueOf(this.id.callback(game, caster, localStore, castId));
	}

}
