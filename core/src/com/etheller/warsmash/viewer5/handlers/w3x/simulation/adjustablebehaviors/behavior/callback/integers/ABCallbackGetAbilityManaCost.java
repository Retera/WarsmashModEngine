package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAbilityManaCost extends ABIntegerCallback {

	private ABAbilityCallback ability;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (ability == null) {
			ABAbilityBuilderActiveAbility abil = (ABAbilityBuilderActiveAbility) localStore.originAbility;
			return abil.getChargedManaCost();
		} else {
			CAbility abil = ability.callback(caster, localStore, castId);
			if (abil instanceof ABAbilityBuilderActiveAbility) {
				return ((ABAbilityBuilderActiveAbility) abil).getChargedManaCost();
			} else {
				return 0;
			}
		}
	}

}
