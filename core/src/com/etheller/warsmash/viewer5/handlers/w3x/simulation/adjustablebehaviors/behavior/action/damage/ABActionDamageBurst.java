package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.damage;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CGenericDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class ABActionDamageBurst implements ABAction {

	private ABUnitCallback source;
	private ABLocationCallback target;
	private ABFloatCallback fullDamageRadius;
	private ABFloatCallback partialDamageRadius;
	private ABFloatCallback damage;
	private ABFloatCallback partialDamage;
	private ABBooleanCallback validTarget;

	private ABFloatCallback unitSpecificDamageMod;
	private ABFloatCallback maxDamage;

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
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CAttackType theAttackType = CAttackType.SPELLS;
		CDamageType theDamageType = CDamageType.MAGIC;
		final CDamageFlags flags = new CGenericDamageFlags(false, true);

		float theDamage = damage.callback(caster, localStore, castId);

		if (isAttack != null) {
			flags.setAttack(isAttack.callback(caster, localStore, castId));
		}
		if (isRanged != null) {
			flags.setRanged(isRanged.callback(caster, localStore, castId));
		}
		if (damageInvulnerable != null) {
			flags.setIgnoreInvulnerable(damageInvulnerable.callback(caster, localStore, castId));
		}
		if (explodeOnDeath != null) {
			flags.setExplode(explodeOnDeath.callback(caster, localStore, castId));
		}
		if (onlyDamageSummons != null) {
			flags.setOnlyDamageSummons(onlyDamageSummons.callback(caster, localStore, castId));
		}
		if (nonlethal != null) {
			flags.setNonlethal(nonlethal.callback(caster, localStore, castId));
		}
		if (this.attackType != null) {
			theAttackType = this.attackType.callback(caster, localStore, castId);
		}
		if (this.damageType != null) {
			theDamageType = this.damageType.callback(caster, localStore, castId);
		}
		if (theDamage > 0 || ignoreLTEZero == null || !ignoreLTEZero.callback(caster, localStore, castId)) {
			AbilityPointTarget loc = target.callback(caster, localStore, castId);
			float pfullRad = 0;
			float parRad = 0;
			float parDam = 0;
			float theMaxDamage = 0;
			CUnit theSource = caster;
			if (this.fullDamageRadius != null) {
				pfullRad = this.fullDamageRadius.callback(caster, localStore, castId);
			}
			if (this.partialDamageRadius != null) {
				parRad = this.partialDamageRadius.callback(caster, localStore, castId);
			}
			if (this.partialDamage != null) {
				parDam = this.partialDamage.callback(caster, localStore, castId);
			}
			if (this.maxDamage != null) {
				theMaxDamage = this.maxDamage.callback(caster, localStore, castId);
			}
			if (this.source != null) {
				theSource = this.source.callback(caster, localStore, castId);
			}

			final float fullRad = pfullRad;
			final float partialRad = parRad;
			final float partialDam = parDam;
			final CAttackType ftheAttackType = theAttackType;
			final CDamageType ftheDamageType = theDamageType;
			final CUnit ftheSource = theSource;
			final float baseDamage = damage.callback(caster, localStore, castId);

			if (theMaxDamage > 0) {
				List<CUnit> fullhits = new ArrayList<>();
				List<CUnit> partialhits = new ArrayList<>();
				List<Float> counts = new ArrayList<>();
				float partialRatio = partialDam / baseDamage;
				localStore.game.getWorldCollision().enumUnitsInRange(loc.getX(), loc.getY(),
						Math.max(pfullRad, partialRad), new CUnitEnumFunction() {
							@Override
							public boolean call(final CUnit enumUnit) {
								localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId),
										enumUnit);
								if (validTarget == null || validTarget.callback(caster, localStore, castId)) {
									if (enumUnit.canReach(loc, fullRad)) {
										fullhits.add(enumUnit);
										if (unitSpecificDamageMod != null) {
											counts.add(unitSpecificDamageMod.callback(caster, localStore, castId));
										} else {
											counts.add(1f);
										}
									} else if (enumUnit.canReach(loc, partialRad)) {
										partialhits.add(enumUnit);
										if (unitSpecificDamageMod != null) {
											counts.add(partialRatio
													* unitSpecificDamageMod.callback(caster, localStore, castId));
										} else {
											counts.add(partialRatio);
										}
									}
								}
								localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId));
								return false;
							}
						});

				float count = 0;
				for (float c : counts) {
					count += c;
				}
				float damPerTar = baseDamage * count > theMaxDamage
						? theMaxDamage / (fullhits.size() + partialRatio * partialhits.size())
						: baseDamage;
				for (CUnit hit : fullhits) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId), hit);
					if (extraActions != null) {
						for (ABAction action : extraActions) {
							action.runAction(caster, localStore, castId);
						}
					}
					if (unitSpecificDamageMod != null) {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(),
								damPerTar * unitSpecificDamageMod.callback(caster, localStore, castId));
					} else {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(), damPerTar);
					}
				}
				for (CUnit hit : partialhits) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId), hit);
					if (extraActions != null) {
						for (ABAction action : extraActions) {
							action.runAction(caster, localStore, castId);
						}
					}
					if (unitSpecificDamageMod != null) {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(),
								damPerTar * partialRatio * unitSpecificDamageMod.callback(caster, localStore, castId));
					} else {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(), damPerTar * partialRatio);
					}
				}
				localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId));

			} else {
				localStore.game.getWorldCollision().enumUnitsInRange(loc.getX(), loc.getY(),
						Math.max(pfullRad, partialRad), new CUnitEnumFunction() {
							@Override
							public boolean call(final CUnit enumUnit) {
								localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId),
										enumUnit);
								if (validTarget == null || validTarget.callback(caster, localStore, castId)) {
									if (enumUnit.canReach(loc, fullRad)) {
										if (extraActions != null) {
											for (ABAction action : extraActions) {
												action.runAction(caster, localStore, castId);
											}
										}
										if (unitSpecificDamageMod != null) {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
													baseDamage * unitSpecificDamageMod.callback(caster, localStore,
															castId));
										} else {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(), baseDamage);
										}
									} else if (enumUnit.canReach(loc, partialRad)) {
										if (extraActions != null) {
											for (ABAction action : extraActions) {
												action.runAction(caster, localStore, castId);
											}
										}
										if (unitSpecificDamageMod != null) {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
													partialDam * unitSpecificDamageMod.callback(caster, localStore,
															castId));
										} else {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(), partialDam);
										}
									}
								}
								localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId));
								return false;
							}
						});
			}

		}
	}

}
