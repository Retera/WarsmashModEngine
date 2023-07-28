package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.listenercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABAttackPostDamageListener;

public class ABCallbackGetStoredAttackPostDamageListenerByKey extends ABAttackPostDamageListenerCallback {
	private ABStringCallback key;

	@Override
	public ABAttackPostDamageListener callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (ABAttackPostDamageListener) localStore.get(key.callback(game, caster, localStore));
	}

}
