package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackCountBuffsOnUnitMatchingCondition extends ABIntegerCallback {

	private ABUnitCallback unit;
	private ABCondition condition;

	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit u = caster;
		if (unit != null) {
			u = unit.callback(game, caster, localStore, castId);
		}
		int c = 0;
		for (CAbility ability : u.getAbilities()) {
			if (ability.getAbilityCategory() == CAbilityCategory.BUFF) {
				CBuff buff = (CBuff) ability;
				localStore.put(ABLocalStoreKeys.MATCHINGBUFF, buff);
				if (condition != null && condition.callback(game, caster, localStore, castId)) {
					c++;
				}
			}
		}
		
		return c;
	}

}
