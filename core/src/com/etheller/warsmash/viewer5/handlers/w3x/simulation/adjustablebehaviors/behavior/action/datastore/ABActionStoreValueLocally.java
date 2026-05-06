package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.datastore;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionStoreValueLocally implements ABSingleAction {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;
	private ABCallback valueToStore;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(caster, localStore, castId)) {
			localStore.put(ABLocalStoreKeys.combineUserInstanceKey(this.key.callback(caster, localStore, castId),
					castId), this.valueToStore.callback(caster, localStore, castId));
		} else {
			localStore.put(ABLocalStoreKeys.combineUserKey(this.key.callback(caster, localStore, castId), castId),
					this.valueToStore.callback(caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String args = jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ", "
				+ this.valueToStore.generateJassEquivalent(jassTextGenerator);

		if (this.valueToStore instanceof ABStringCallback) {
			if (this.instanceValue == null) {
				return "SetLocalStoreUserCastStringAU(" + args + ")";
			} else {
				return "StoreStringLocallyAU(" + args + ", "
						+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
			}
		} else if ((this.valueToStore instanceof ABIntegerCallback) || (this.valueToStore instanceof ABIDCallback)) {
			if (this.instanceValue == null) {
				return "SetLocalStoreUserCastIntegerAU(" + args + ")";
			} else {
				return "StoreIntegerLocallyAU(" + args + ", "
						+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
			}
		} else if (this.valueToStore instanceof ABBooleanCallback) {
			if (this.instanceValue == null) {
				return "SetLocalStoreUserCastBooleanAU(" + args + ")";
			} else {
				return "StoreBooleanLocallyAU(" + args + ", "
						+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
			}
		} else if (this.valueToStore instanceof ABFloatCallback) {
			if (this.instanceValue == null) {
				return "SetLocalStoreUserCastRealAU(" + args + ")";
			} else {
				return "StoreRealLocallyAU(" + args + ", "
						+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
			}
		} else {
			if (this.instanceValue == null) {
				return "SetLocalStoreUserCastHandleAU(" + args + ")";
			} else {
				return "StoreHandleLocallyAU(" + args + ", "
						+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
			}
		}
	}
}
