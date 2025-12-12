package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;

public class ABCallbackGetAbilityUniqueValueId extends ABIDCallback {

	private ABAbilityCallback ability;
	private ABStringCallback key;
	
	private ABBooleanCallback allowNull;

	@Override
	public War3ID callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final String keyS = key.callback(game, caster, localStore, castId);
		final CAbility theAbility = ability.callback(game, caster, localStore, castId);
		War3ID theVal = theAbility.getUniqueValue(keyS, War3ID.class);
		System.err.println("Get Unique ID Val: " + (theVal != null ? theVal.asStringValue() : null));
		if (theVal != null || (allowNull != null && allowNull.callback(game, caster, localStore, castId))) {
			return theVal;
		}
		return War3ID.NONE;
	}

}
