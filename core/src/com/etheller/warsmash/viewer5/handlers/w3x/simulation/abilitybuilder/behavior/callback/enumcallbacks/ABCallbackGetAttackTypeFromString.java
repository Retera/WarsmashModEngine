package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;

public class ABCallbackGetAttackTypeFromString extends ABAttackTypeCallback {

	private ABStringCallback id;
	
	@Override
	public CAttackType callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return CAttackType.valueOf(id.callback(game, caster, localStore));
	}

}
