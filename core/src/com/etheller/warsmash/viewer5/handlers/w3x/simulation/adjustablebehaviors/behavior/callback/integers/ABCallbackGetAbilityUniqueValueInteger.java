package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAbilityUniqueValueInteger extends ABIntegerCallback {

	private ABAbilityCallback ability;
	private ABStringCallback key;
	
	private ABBooleanCallback allowNull;

	@Override
	public Integer callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final String keyS = key.callback(caster, localStore, castId);
		final CAbility theAbility = ability.callback(caster, localStore, castId);
		Integer theVal = theAbility.getUniqueValue(keyS, Integer.class);
		if (theVal != null || (allowNull != null && allowNull.callback(caster, localStore, castId))) {
			return theVal;
		}
		return 0;
	}

}
