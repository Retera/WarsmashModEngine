package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABCallbackRawString;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class ABCallbackGetWeaponTypeFromString extends ABWeaponTypeCallback {

	private ABStringCallback id;

	@Override
	public CWeaponType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return CWeaponType.valueOf(this.id.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		if (this.id instanceof ABCallbackRawString) {
			// if possible, convert it directly, no shenanigans
			final String value = ((ABCallbackRawString) this.id).getValue();
			return "WEAPON_TYPE_" + value;
		}
		return "String2WeaponType(" + this.id.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
