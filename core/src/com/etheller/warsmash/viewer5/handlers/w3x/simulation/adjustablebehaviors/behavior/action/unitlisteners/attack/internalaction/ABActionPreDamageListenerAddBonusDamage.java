
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.attack.internalaction;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABActionPreDamageListenerAddBonusDamage implements ABAction {

	private ABFloatCallback value;
	private ABDamageTypeCallback damageType;
	private ABAttackTypeCallback attackType;

	private ABBooleanCallback inheritFlags;
	private ABBooleanCallback isRanged;

	private ABBooleanCallback isIgnoreInvulnerable;
	private ABBooleanCallback isExplode;
	private ABBooleanCallback isOnlyDamageSummons;
	private ABBooleanCallback isNonlethal;
	private ABBooleanCallback isPassLimitedMagicImmune;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CDamageType theDamageType = null;
		if (damageType != null) {
			theDamageType = damageType.callback(caster, localStore, castId);
		}
		CAttackType theAttackType = null;
		if (attackType != null) {
			theAttackType = attackType.callback(caster, localStore, castId);
		}

		CDamageCalculation damage = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, castId),
				CDamageCalculation.class);

		CDamageFlags theFlags = null;
		if (inheritFlags == null || inheritFlags.callback(caster, localStore, castId)) {
			theFlags = damage.getPrimaryDamageFlags().copy();
		} else if (isRanged != null || isIgnoreInvulnerable != null || isExplode != null || isOnlyDamageSummons != null
				|| isNonlethal != null || isPassLimitedMagicImmune != null) {
			boolean ranged;
			if (isRanged == null) {
				ranged = damage.getPrimaryDamageFlags().isRanged();
			} else {
				ranged = isRanged.callback(caster, localStore, castId);
			}
			theFlags = new CAttackDamageFlags(ranged);
		}

		if (isIgnoreInvulnerable != null) {
			theFlags.setIgnoreInvulnerable(isIgnoreInvulnerable.callback(caster, localStore, castId));
		}
		if (isExplode != null) {
			theFlags.setExplode(isExplode.callback(caster, localStore, castId));
		}
		if (isOnlyDamageSummons != null) {
			theFlags.setOnlyDamageSummons(isOnlyDamageSummons.callback(caster, localStore, castId));
		}
		if (isNonlethal != null) {
			theFlags.setNonlethal(isNonlethal.callback(caster, localStore, castId));
		}
		if (isPassLimitedMagicImmune != null) {
			theFlags.setPassLimitedMagicImmune(isPassLimitedMagicImmune.callback(caster, localStore, castId));
		}

		damage.addBonusDamage(value.callback(caster, localStore, castId), theAttackType, theDamageType, theFlags);
	}
}