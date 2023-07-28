package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class ABCallbackGetNonStackingStatBuffTypeFromString extends ABNonStackingStatBuffTypeCallback {

	private ABStringCallback id;
	
	@Override
	public NonStackingStatBuffType callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return NonStackingStatBuffType.valueOf(id.callback(game, caster, localStore));
	}

}
