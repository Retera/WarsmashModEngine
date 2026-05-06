package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.attacksettings;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABCallbackGetLastCreatedAttackSettings extends ABAttackSettingsCallback {

	@Override
	public CUnitAttackSettings callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.get(ABLocalStoreKeys.LASTCREATEDASettings, CUnitAttackSettings.class);
	}

}
