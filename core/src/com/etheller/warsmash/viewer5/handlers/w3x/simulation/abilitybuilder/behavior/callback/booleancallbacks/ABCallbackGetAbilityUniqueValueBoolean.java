package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;

public class ABCallbackGetAbilityUniqueValueBoolean extends ABBooleanCallback {

	private ABAbilityCallback ability;
	private ABStringCallback key;
	
	private ABBooleanCallback allowNull;

	@Override
	public Boolean callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final String keyS = key.callback(game, caster, localStore, castId);
		final CAbility theAbility = ability.callback(game, caster, localStore, castId);
		Boolean theVal = theAbility.getUniqueValue(keyS, Boolean.class);
		if (theVal != null || (allowNull != null && allowNull.callback(game, caster, localStore, castId))) {
			return theVal;
		}
		return false;
	}

}
