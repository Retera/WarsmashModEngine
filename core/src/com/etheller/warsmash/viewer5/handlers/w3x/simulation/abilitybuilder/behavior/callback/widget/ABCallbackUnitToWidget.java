package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.widget;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackUnitToWidget extends ABWidgetCallback {

	private ABUnitCallback unit;
	
	@Override
	public CWidget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return unit.callback(game, caster, localStore, castId);
	}

}
