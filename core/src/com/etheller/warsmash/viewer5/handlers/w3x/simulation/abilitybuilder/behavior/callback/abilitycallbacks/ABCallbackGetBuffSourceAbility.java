package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.ABBuffCallback;

public class ABCallbackGetBuffSourceAbility extends ABAbilityCallback {

	private ABBuffCallback buff;
	
	@Override
	public CAbility callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return buff.callback(game, caster, localStore, castId).getSourceAbility();
	}

}
