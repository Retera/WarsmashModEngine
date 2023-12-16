package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetAbilityCastRange extends ABFloatCallback {

	private ABAbilityCallback ability;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (ability == null) {
			AbilityBuilderActiveAbility abil = (AbilityBuilderActiveAbility) localStore.get(ABLocalStoreKeys.ABILITY);
			return abil.getCastRange();
		} else {
			CAbility abil = ability.callback(game, caster, localStore, castId);
			if (abil instanceof AbilityBuilderActiveAbility) {
				return ((AbilityBuilderActiveAbility)abil).getCastRange();
			} else {
				return 0f;
			}
		}
	}

}
