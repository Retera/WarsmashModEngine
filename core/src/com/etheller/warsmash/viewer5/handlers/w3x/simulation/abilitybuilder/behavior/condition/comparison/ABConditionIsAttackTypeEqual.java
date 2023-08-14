package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.comparison;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;

public class ABConditionIsAttackTypeEqual implements ABCondition {

	private ABAttackTypeCallback attackType1;
	private ABAttackTypeCallback attackType2;

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CAttackType lA = attackType1.callback(game, caster, localStore, castId);
		CAttackType rA = attackType2.callback(game, caster, localStore, castId);
		if (lA == null) {
			if (rA == null) {
				return true;
			}
			return false;
		}
		return lA.equals(rA);
	}

}
