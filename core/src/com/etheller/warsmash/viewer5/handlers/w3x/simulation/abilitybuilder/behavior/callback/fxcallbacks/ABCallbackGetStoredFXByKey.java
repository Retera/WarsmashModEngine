package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.fxcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABCallbackGetStoredFXByKey extends ABFXCallback {

	private ABStringCallback key;
	
	@Override
	public SimulationRenderComponent callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (SimulationRenderComponent) localStore.get(key.callback(game, caster, localStore));
	}

}
