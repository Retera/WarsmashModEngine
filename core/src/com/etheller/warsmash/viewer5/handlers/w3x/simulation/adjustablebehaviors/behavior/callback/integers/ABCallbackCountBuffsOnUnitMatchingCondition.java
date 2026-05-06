package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackCountBuffsOnUnitMatchingCondition extends ABIntegerCallback {

	private ABUnitCallback unit;
	private ABBooleanCallback condition;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit u = caster;
		if (unit != null) {
			u = unit.callback(caster, localStore, castId);
		}
		int c = 0;
		for (CAbility ability : u.getAbilities()) {
			if (ability.getAbilityCategory() == CAbilityCategory.BUFF) {
				CBuff buff = (CBuff) ability;
				localStore.put(ABLocalStoreKeys.MATCHINGBUFF, buff);
				if (condition != null && condition.callback(caster, localStore, castId)) {
					c++;
				}
			}
		}
		
		return c;
	}

}
