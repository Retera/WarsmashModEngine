package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;

public class ABCallbackGetStoredAttackTypeByKey extends ABAttackTypeCallback {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public CAttackType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(game, caster, localStore, castId)) {
			return (CAttackType) localStore.get(ABLocalStoreKeys
					.combineUserInstanceKey(this.key.callback(game, caster, localStore, castId), castId));
		}
		else {
			return (CAttackType) localStore
					.get(ABLocalStoreKeys.combineUserKey(this.key.callback(game, caster, localStore, castId), castId));
		}
	}

}
