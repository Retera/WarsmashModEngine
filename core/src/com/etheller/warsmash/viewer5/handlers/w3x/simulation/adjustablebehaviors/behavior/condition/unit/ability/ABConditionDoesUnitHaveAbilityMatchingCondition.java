package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.unit.ability;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABConditionDoesUnitHaveAbilityMatchingCondition extends ABBooleanCallback {

	private ABUnitCallback unit;
	private ABBooleanCallback condition;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(caster, localStore, castId);
		}
		if (theUnit != null) {
			for (CAbility ability : theUnit.getAbilities()) {
				if (CAbilityCategory.BUFF != ability.getAbilityCategory()) {
					localStore.put(ABLocalStoreKeys.MATCHINGABILITY, ability);
					if (condition.callback(caster, localStore, castId)) {
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
