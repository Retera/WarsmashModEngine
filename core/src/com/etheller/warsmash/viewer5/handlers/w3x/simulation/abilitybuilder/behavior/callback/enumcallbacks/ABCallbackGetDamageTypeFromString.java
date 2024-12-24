package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABCallbackRawString;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABCallbackGetDamageTypeFromString extends ABDamageTypeCallback {

	private ABStringCallback id;

	@Override
	public CDamageType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return CDamageType.valueOf(this.id.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		if (this.id instanceof ABCallbackRawString) {
			// if possible, convert it directly, no shenanigans
			final String value = ((ABCallbackRawString) this.id).getValue();
			return "DAMAGE_TYPE_" + value;
		}
		return "String2DamageType(" + this.id.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
