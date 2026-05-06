package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.attack.internalcondition;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class ABConditionIsUnitValidSplashDamageTarget extends ABBooleanCallback {

	private ABUnitCallback caster;
	private ABUnitCallback unit;
	private ABBooleanCallback targetedEffect;

	@Override
	public Boolean callback(CUnit casterUnit, ABLocalDataStore localStore, final int castId) {
		CUnit theCaster = casterUnit;

		EnumSet<CTargetType> targetsAllowed = null;

		CUnitAttack atk = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, castId), CUnitAttack.class);
		if (atk instanceof CUnitAttackMissileSplash) {
			targetsAllowed = ((CUnitAttackMissileSplash) atk).getAreaOfEffectTargets();
		} else {
			return false;
		}

		if (targetsAllowed.isEmpty()) {
			return true;
		}
		final CUnit theUnit = this.unit.callback(casterUnit, localStore, castId);
		boolean te = false;
		if (this.caster != null) {
			theCaster = this.caster.callback(casterUnit, localStore, castId);
		}
		if (this.targetedEffect != null) {
			te = this.targetedEffect.callback(theCaster, localStore, castId);
		}

		return theUnit.canBeTargetedBy(localStore.game, theCaster, te, targetsAllowed,
				BooleanAbilityTargetCheckReceiver.<CWidget>getInstance().reset());
	}

}
