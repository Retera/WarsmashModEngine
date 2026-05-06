package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.attack.internalcallback;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABCallbackGetAttackMaximumSplashRadius extends ABIntegerCallback {

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnitAttackSettings settings = localStore
				.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKSETTINGS, castId), CUnitAttackSettings.class);
		return Math.max(settings.getAreaOfEffectFullDamage(),
				Math.max(settings.getAreaOfEffectMediumDamage(), settings.getAreaOfEffectSmallDamage()));
	}

}
