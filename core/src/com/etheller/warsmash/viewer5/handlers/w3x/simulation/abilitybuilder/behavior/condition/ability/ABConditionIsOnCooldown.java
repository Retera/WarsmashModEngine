package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.ability;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABConditionIsOnCooldown implements ABCondition {

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		War3ID alias = (War3ID) localStore.get(ABLocalStoreKeys.ALIAS);

		return caster.getCooldownRemainingTicks(game, alias) > 0;
	}

}
