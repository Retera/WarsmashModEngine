
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalActions;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;

public class ABActionPreDamageListenerSetMiss implements ABAction {

	private ABBooleanCallback miss;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CUnitAttackPreDamageListenerDamageModResult result = (CUnitAttackPreDamageListenerDamageModResult) localStore
				.get(ABLocalStoreKeys.PREDAMAGERESULT+castId);
		if (miss != null) {
			result.setMiss(miss.callback(game, caster, localStore, castId));
		} else {
			result.setMiss(true);
		}
	}
}