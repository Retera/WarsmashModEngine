package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalConditions;

import java.util.EnumSet;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsUnitValidSplashDamageTarget extends ABCondition {

	private ABUnitCallback caster;
	private ABUnitCallback unit;
	private ABBooleanCallback targetedEffect;

	@Override
	public Boolean callback(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		CUnit theCaster = casterUnit;

		EnumSet<CTargetType> targetsAllowed = null;
		
		CUnitAttack atk = (CUnitAttack) localStore.get(ABLocalStoreKeys.THEATTACK+castId);
		if (atk instanceof CUnitAttackMissileSplash) {
			targetsAllowed = ((CUnitAttackMissileSplash)atk).getAreaOfEffectTargets();
		} else {
			return false;
		}
		
		if (targetsAllowed.isEmpty()) {
			return true;
		}
		final CUnit theUnit = this.unit.callback(game, casterUnit, localStore, castId);
		boolean te = false;
		if (this.caster != null) {
			theCaster = this.caster.callback(game, casterUnit, localStore, castId);
		}
		if (this.targetedEffect != null) {
			te = this.targetedEffect.callback(game, theCaster, localStore, castId);
		}

		return theUnit.canBeTargetedBy(game, theCaster, te, targetsAllowed,
				BooleanAbilityTargetCheckReceiver.<CWidget>getInstance().reset());
	}

}
