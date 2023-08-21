package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeysEnum;

public class ABConditionSetCantUseReasonOnFailure implements ABCondition {

	private ABCondition condition;
	private CommandStringErrorKeysEnum reason;

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		boolean result = condition.evaluate(game, caster, localStore, castId);
		if (!result) {
			localStore.put(ABLocalStoreKeys.CANTUSEREASON, reason.getKey());
		}
		return result;
	}

}
