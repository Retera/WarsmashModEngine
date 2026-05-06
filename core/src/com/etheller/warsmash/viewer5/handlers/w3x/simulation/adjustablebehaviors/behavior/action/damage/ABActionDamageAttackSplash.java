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

public class ABActionDamageAttackSplash implements ABAction {

	private ABUnitCallback source;
	private ABLocationCallback target;
	private ABFloatCallback damage;
	private ABFloatCallback fullDamageRadius;
	private ABFloatCallback mediumDamageRadius;
	private ABFloatCallback smallDamageRadius;
	private ABFloatCallback damageFactorMedium;
	private ABFloatCallback damageFactorSmall;
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
			float pmedRad = 0;
			float psmallRad = 0;
			float pmedDam = 0;
			float psmallDam = 0;
			float theMaxDamage = 0;
			CUnit theSource = caster;
			if (this.fullDamageRadius != null) {
				pfullRad = this.fullDamageRadius.callback(caster, localStore, castId);
			}
			if (this.mediumDamageRadius != null) {
				pmedRad = this.mediumDamageRadius.callback(caster, localStore, castId);
			}
			if (this.smallDamageRadius != null) {
				psmallRad = this.smallDamageRadius.callback(caster, localStore, castId);
			}
			if (this.damageFactorMedium != null) {
				pmedDam = this.damageFactorMedium.callback(caster, localStore, castId);
			}
			if (this.damageFactorSmall != null) {
				psmallDam = this.damageFactorSmall.callback(caster, localStore, castId);
			}
			if (this.maxDamage != null) {
				theMaxDamage = this.maxDamage.callback(caster, localStore, castId);
			}
			if (this.source != null) {
				theSource = this.source.callback(caster, localStore, castId);
			}

			final float fullRad = pfullRad;
			final float medRad = pmedRad;
			final float smallRad = psmallRad;
			final float medDam = pmedDam;
			final float smallDam = psmallDam;
			final CAttackType ftheAttackType = theAttackType;
			final CDamageType ftheDamageType = theDamageType;
			final CUnit ftheSource = theSource;
			final float baseDamage = damage.callback(caster, localStore, castId);

			if (theMaxDamage > 0) {
				List<CUnit> fullhits = new ArrayList<>();
				List<CUnit> medhits = new ArrayList<>();
				List<CUnit> smallhits = new ArrayList<>();
				List<Float> counts = new ArrayList<>();
				localStore.game.getWorldCollision().enumUnitsInRange(loc.getX(), loc.getY(),
						Math.max(pfullRad, Math.max(medRad, smallRad)), new CUnitEnumFunction() {
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
									} else if (enumUnit.canReach(loc, medRad)) {
										medhits.add(enumUnit);
										if (unitSpecificDamageMod != null) {
											counts.add(medDam
													* unitSpecificDamageMod.callback(caster, localStore, castId));
										} else {
											counts.add(medDam);
										}
									} else if (enumUnit.canReach(loc, smallRad)) {
										smallhits.add(enumUnit);
										if (unitSpecificDamageMod != null) {
											counts.add(smallDam
													* unitSpecificDamageMod.callback(caster, localStore, castId));
										} else {
											counts.add(smallDam);
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
						? theMaxDamage / (fullhits.size() + medDam * medhits.size() + smallDam * smallhits.size())
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
				for (CUnit hit : medhits) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId), hit);
					if (extraActions != null) {
						for (ABAction action : extraActions) {
							action.runAction(caster, localStore, castId);
						}
					}
					if (unitSpecificDamageMod != null) {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(),
								damPerTar * medDam * unitSpecificDamageMod.callback(caster, localStore, castId));
					} else {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(), damPerTar * medDam);
					}
				}
				for (CUnit hit : smallhits) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId), hit);
					if (extraActions != null) {
						for (ABAction action : extraActions) {
							action.runAction(caster, localStore, castId);
						}
					}
					if (unitSpecificDamageMod != null) {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(),
								damPerTar * smallDam * unitSpecificDamageMod.callback(caster, localStore, castId));
					} else {
						hit.damage(localStore.game, ftheSource, flags, ftheAttackType, ftheDamageType,
								CWeaponSoundTypeJass.WHOKNOWS.name(), damPerTar * smallDam);
					}
				}
				localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId));

			} else {
				localStore.game.getWorldCollision().enumUnitsInRange(loc.getX(), loc.getY(),
						Math.max(Math.max(pfullRad, pmedRad), psmallRad), new CUnitEnumFunction() {
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
									} else if (enumUnit.canReach(loc, medRad)) {
										if (extraActions != null) {
											for (ABAction action : extraActions) {
												action.runAction(caster, localStore, castId);
											}
										}
										if (unitSpecificDamageMod != null) {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
													baseDamage * medDam * unitSpecificDamageMod.callback(caster,
															localStore, castId));
										} else {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
													baseDamage * medDam);
										}
									} else if (enumUnit.canReach(loc, smallRad)) {
										if (extraActions != null) {
											for (ABAction action : extraActions) {
												action.runAction(caster, localStore, castId);
											}
										}
										if (unitSpecificDamageMod != null) {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
													baseDamage * smallDam * unitSpecificDamageMod.callback(caster,
															localStore, castId));
										} else {
											enumUnit.damage(localStore.game, ftheSource, flags, ftheAttackType,
													ftheDamageType, CWeaponSoundTypeJass.WHOKNOWS.name(),
													baseDamage * smallDam);
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
