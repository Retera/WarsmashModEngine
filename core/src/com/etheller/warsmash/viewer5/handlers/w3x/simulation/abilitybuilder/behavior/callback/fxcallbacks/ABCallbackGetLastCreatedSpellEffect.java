package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.fxcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABCallbackGetLastCreatedSpellEffect extends ABFXCallback {

	@Override
	public SimulationRenderComponent callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (SimulationRenderComponent) localStore.get(ABLocalStoreKeys.LASTCREATEDFX);
	}

}
