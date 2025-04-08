
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.attack.internal;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABActionAttackModifierSetSplashFields implements ABAction {

	private ABIntegerCallback areaOfEffectFullDamage;
	private ABIntegerCallback areaOfEffectMediumDamage;
	private ABIntegerCallback areaOfEffectSmallDamage;
	private ABFloatCallback damageFactorMedium;
	private ABFloatCallback damageFactorSmall;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnitAttackSettings settings = (CUnitAttackSettings) localStore.get(ABLocalStoreKeys.ATTACKSETTINGS + castId);
		if (areaOfEffectFullDamage != null) {
			settings.setAreaOfEffectFullDamage(areaOfEffectFullDamage.callback(game, caster, localStore, castId));
		}
		if (areaOfEffectMediumDamage != null) {
			settings.setAreaOfEffectMediumDamage(areaOfEffectMediumDamage.callback(game, caster, localStore, castId));
		}
		if (areaOfEffectSmallDamage != null) {
			settings.setAreaOfEffectSmallDamage(areaOfEffectSmallDamage.callback(game, caster, localStore, castId));
		}
		if (damageFactorMedium != null) {
			settings.setDamageFactorMedium(damageFactorMedium.callback(game, caster, localStore, castId));
		}
		if (damageFactorSmall != null) {
			settings.setDamageFactorSmall(damageFactorSmall.callback(game, caster, localStore, castId));
		}
	}
}