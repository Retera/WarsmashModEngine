package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unit;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABConditionDoesUnitHaveAbilityMatchingCondition extends ABCondition {

	private ABUnitCallback unit;
	private ABBooleanCallback condition;

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(game, caster, localStore, castId);
		}
		if (theUnit != null) {
			for (CAbility ability : theUnit.getAbilities()) {
				if (CAbilityCategory.BUFF != ability.getAbilityCategory()) {
					localStore.put(ABLocalStoreKeys.MATCHINGABILITY, ability);
					if (condition.callback(game, caster, localStore, castId)) {
						localStore.remove(ABLocalStoreKeys.MATCHINGABILITY);
						return true;
					}
				}
			}
		}
		localStore.remove(ABLocalStoreKeys.MATCHINGABILITY);
		return false;
	}

}
