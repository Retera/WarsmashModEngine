package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAbilityUniqueValueId extends ABIDCallback {

	private ABAbilityCallback ability;
	private ABStringCallback key;

	private ABBooleanCallback allowNull;

	@Override
	public War3ID callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final String keyS = key.callback(caster, localStore, castId);
		final CAbility theAbility = ability.callback(caster, localStore, castId);
		War3ID theVal = theAbility.getUniqueValue(keyS, War3ID.class);
		System.err.println("Get Unique ID Val: " + (theVal != null ? theVal.asStringValue() : null));
		if (theVal != null || (allowNull != null && allowNull.callback(caster, localStore, castId))) {
			return theVal;
		}
		return War3ID.NONE;
	}

}
