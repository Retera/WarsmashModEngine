package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAbilityCooldown extends ABFloatCallback {

	private ABAbilityCallback ability;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (ability == null) {
			ABAbilityBuilderAbility abil = localStore.originAbility;
			return abil.getCooldown();
		} else {
			CAbility abil = ability.callback(caster, localStore, castId);
			if (abil instanceof ABAbilityBuilderAbility) {
				return ((ABAbilityBuilderAbility) abil).getCooldown();
			} else {
				return 0f;
			}
		}
	}

}
