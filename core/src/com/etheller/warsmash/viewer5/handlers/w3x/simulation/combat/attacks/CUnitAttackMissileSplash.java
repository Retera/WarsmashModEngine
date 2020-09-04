package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class CUnitAttackMissileSplash extends CUnitAttackMissile {
	private int areaOfEffectFullDamage;
	private int areaOfEffectMediumDamage;
	private int areaOfEffectSmallDamage;
	private EnumSet<CTargetType> areaOfEffectTargets;
	private float damageFactorMedium;
	private float damageFactorSmall;

	public CUnitAttackMissileSplash(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final int areaOfEffectFullDamage,
			final int areaOfEffectMediumDamage, final int areaOfEffectSmallDamage,
			final EnumSet<CTargetType> areaOfEffectTargets, final float damageFactorMedium,
			final float damageFactorSmall) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed);
		this.areaOfEffectFullDamage = areaOfEffectFullDamage;
		this.areaOfEffectMediumDamage = areaOfEffectMediumDamage;
		this.areaOfEffectSmallDamage = areaOfEffectSmallDamage;
		this.areaOfEffectTargets = areaOfEffectTargets;
		this.damageFactorMedium = damageFactorMedium;
		this.damageFactorSmall = damageFactorSmall;
	}

	public int getAreaOfEffectFullDamage() {
		return this.areaOfEffectFullDamage;
	}

	public int getAreaOfEffectMediumDamage() {
		return this.areaOfEffectMediumDamage;
	}

	public int getAreaOfEffectSmallDamage() {
		return this.areaOfEffectSmallDamage;
	}

	public EnumSet<CTargetType> getAreaOfEffectTargets() {
		return this.areaOfEffectTargets;
	}

	public float getDamageFactorMedium() {
		return this.damageFactorMedium;
	}

	public float getDamageFactorSmall() {
		return this.damageFactorSmall;
	}

	public void setAreaOfEffectFullDamage(final int areaOfEffectFullDamage) {
		this.areaOfEffectFullDamage = areaOfEffectFullDamage;
	}

	public void setAreaOfEffectMediumDamage(final int areaOfEffectMediumDamage) {
		this.areaOfEffectMediumDamage = areaOfEffectMediumDamage;
	}

	public void setAreaOfEffectSmallDamage(final int areaOfEffectSmallDamage) {
		this.areaOfEffectSmallDamage = areaOfEffectSmallDamage;
	}

	public void setAreaOfEffectTargets(final EnumSet<CTargetType> areaOfEffectTargets) {
		this.areaOfEffectTargets = areaOfEffectTargets;
	}

	public void setDamageFactorMedium(final float damageFactorMedium) {
		this.damageFactorMedium = damageFactorMedium;
	}

	public void setDamageFactorSmall(final float damageFactorSmall) {
		this.damageFactorSmall = damageFactorSmall;
	}

}
