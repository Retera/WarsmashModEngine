
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.attack.internal;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABActionAttackModifierApplyAllArtFields implements ABAction {

	private ABBooleanCallback applyArtIfMissing;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnitAttackSettings settings = (CUnitAttackSettings) localStore.get(ABLocalStoreKeys.ATTACKSETTINGS + castId);
		AbilityBuilderAbility abil = (AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY);
		String art = abil.getAbilityStringField("Missileart");
		if ((art != null && !art.isBlank()) || (applyArtIfMissing != null && applyArtIfMissing.callback(game, caster, localStore, castId))) {
			settings.setProjectileArt(art);
		}
		settings.setProjectileSpeed(abil.getAbilityIntField("Missilespeed"));
		settings.setProjectileArc(abil.getAbilityFloatField("Missilearc"));
		settings.setProjectileHomingEnabled(abil.getAbilityBooleanField("MissileHoming"));
	}
}