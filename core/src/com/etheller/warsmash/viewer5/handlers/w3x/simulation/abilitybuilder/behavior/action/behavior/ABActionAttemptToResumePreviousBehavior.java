package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.behavior;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionAttemptToResumePreviousBehavior implements ABAction {

	private ABBooleanCallback checkForOrders;
	
	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		if (checkForOrders == null || checkForOrders.callback(game, caster, localStore, castId)) {
			if (caster.getOrderQueue().isEmpty()) {
				localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, localStore.get(ABLocalStoreKeys.PREVIOUSBEHAVIOR));
			}
		} else {
			localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, localStore.get(ABLocalStoreKeys.PREVIOUSBEHAVIOR));
		}
	}

}
