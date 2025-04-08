package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CGenericDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class ABActionDamageArea implements ABAction {

	private ABUnitCallback source;
	private ABLocationCallback target;
	private ABFloatCallback radius;
	private ABFloatCallback damage;
	private ABBooleanCallback validTarget;

	private ABBooleanCallback isAttack;
	private ABBooleanCallback isRanged;
	private ABAttackTypeCallback attackType;
	private ABDamageTypeCallback damageType;

	private ABBooleanCallback ignoreLTEZero;
	private ABBooleanCallback damageInvulnerable;
	private ABBooleanCallback explodeOnDeath;
	private ABBooleanCallback onlyDamageSummons;
	private ABBooleanCallback nonlethal;
	
	private List<ABAction> extraActions;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;
		final CDamageFlags flags = new CGenericDamageFlags(false, true);

		float theDamage = damage.callback(game, caster, localStore, castId);

		if (isAttack != null) {
			flags.setAttack(isAttack.callback(game, caster, localStore, castId));
		}
		if (isRanged != null) {
			flags.setRanged(isRanged.callback(game, caster, localStore, castId));
		}
		if (damageInvulnerable != null) {
			flags.setIgnoreInvulnerable(damageInvulnerable.callback(game, caster, localStore, castId));
		}
		if (explodeOnDeath != null) {
			flags.setExplode(explodeOnDeath.callback(game, caster, localStore, castId));
		}
		if (onlyDamageSummons != null) {
			flags.setOnlyDamageSummons(onlyDamageSummons.callback(game, caster, localStore, castId));
		}
		if (nonlethal != null) {
			flags.setNonlethal(nonlethal.callback(game, caster, localStore, castId));
		}
		if (this.attackType != null) {
			theAttackType = this.attackType.callback(game, caster, localStore, castId);
		}
		if (this.damageType != null) {
			theDamageType = this.damageType.callback(game, caster, localStore, castId);
		}
		if (theDamage > 0 || ignoreLTEZero == null || !ignoreLTEZero.callback(game, caster, localStore, castId)) {
			AbilityPointTarget loc = target.callback(game, caster, localStore, castId);
			float pfullRad = 0;
			CUnit theSource = caster;
			if (this.radius != null) {
				pfullRad = this.radius.callback(game, caster, localStore, castId);
			}
			if (this.source != null) {
				theSource = this.source.callback(game, caster, localStore, castId);
			}

			final float fullRad = pfullRad;
			final CAttackType ftheAttackType = theAttackType;
			final CDamageType ftheDamageType = theDamageType;
			final CUnit ftheSource = theSource;
			final float baseDamage = damage.callback(game, caster, localStore, castId);

			game.getWorldCollision().enumUnitsInRange(loc.getX(), loc.getY(), pfullRad,
					new CUnitEnumFunction() {
						@Override
						public boolean call(final CUnit enumUnit) {
							localStore.put(ABLocalStoreKeys.ENUMUNIT + castId, enumUnit);
							if (validTarget == null || validTarget.callback(game, caster, localStore, castId)) {
								if (enumUnit.canReach(loc, fullRad)) {
									enumUnit.damage(game, ftheSource, flags, ftheAttackType, ftheDamageType,
											CWeaponSoundTypeJass.WHOKNOWS.name(), baseDamage);
									if (extraActions != null) {
										for (ABAction action : extraActions) {
											action.runAction(game, caster, localStore, castId);
										}
									}
								}
							}
							localStore.remove(ABLocalStoreKeys.ENUMUNIT + castId);
							return false;
						}
					});

		}
	}

}
