package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABCallbackRawString;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class ABCallbackGetNonStackingStatBuffTypeFromString extends ABNonStackingStatBuffTypeCallback {

	private ABStringCallback id;

	@Override
	public NonStackingStatBuffType callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		return NonStackingStatBuffType.valueOf(this.id.callback(caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		if (this.id instanceof ABCallbackRawString) {
			// if possible, convert it directly, no shenanigans
			final String value = ((ABCallbackRawString) this.id).getValue();
			return "NON_STACKING_STAT_BUFF_TYPE_" + value;
		}
		return "String2NonStackingStatBuffType(" + this.id.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
