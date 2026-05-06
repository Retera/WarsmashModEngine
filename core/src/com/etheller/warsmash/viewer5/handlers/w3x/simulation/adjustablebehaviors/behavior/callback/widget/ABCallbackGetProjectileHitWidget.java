package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.widget;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetProjectileHitWidget extends ABWidgetCallback {

	@Override
	public CWidget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final CWidget unit = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId),
				CWidget.class);
		final CWidget dest = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId),
				CWidget.class);
		if (unit != null) {
			return unit;
		} else {
			return dest;
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "GetProjectileHitWidgetAU()";
	}

}
