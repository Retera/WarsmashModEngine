package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.comparison;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABConditionIsDamageTypeEqual implements ABCondition {

	private ABDamageTypeCallback damageType1;
	private ABDamageTypeCallback damageType2;

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CDamageType lD = damageType1.callback(game, caster, localStore, castId);
		CDamageType rD = damageType2.callback(game, caster, localStore, castId);
		if (lD == null) {
			if (rD == null) {
				return true;
			}
			return false;
		}
		return lD.equals(rD);
	}

}
