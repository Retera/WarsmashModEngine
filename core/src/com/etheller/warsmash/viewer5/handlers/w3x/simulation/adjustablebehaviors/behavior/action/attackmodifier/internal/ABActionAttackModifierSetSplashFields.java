
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier.internal;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABActionAttackModifierSetSplashFields implements ABAction {

	private ABIntegerCallback areaOfEffectFullDamage;
	private ABIntegerCallback areaOfEffectMediumDamage;
	private ABIntegerCallback areaOfEffectSmallDamage;
	private ABFloatCallback damageFactorMedium;
	private ABFloatCallback damageFactorSmall;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnitAttackSettings settings = localStore
				.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKSETTINGS, castId), CUnitAttackSettings.class);
		if (areaOfEffectFullDamage != null) {
			settings.setAreaOfEffectFullDamage(areaOfEffectFullDamage.callback(caster, localStore, castId));
		}
		if (areaOfEffectMediumDamage != null) {
			settings.setAreaOfEffectMediumDamage(areaOfEffectMediumDamage.callback(caster, localStore, castId));
		}
		if (areaOfEffectSmallDamage != null) {
			settings.setAreaOfEffectSmallDamage(areaOfEffectSmallDamage.callback(caster, localStore, castId));
		}
		if (damageFactorMedium != null) {
			settings.setDamageFactorMedium(damageFactorMedium.callback(caster, localStore, castId));
		}
		if (damageFactorSmall != null) {
			settings.setDamageFactorSmall(damageFactorSmall.callback(caster, localStore, castId));
		}
	}
}