package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class CUnitAttackMissileLine extends CUnitAttackMissile {
	private float damageSpillDistance;
	private float damageSpillRadius;

	public CUnitAttackMissileLine(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final float damageSpillDistance,
			final float damageSpillRadius) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed);
		this.damageSpillDistance = damageSpillDistance;
		this.damageSpillRadius = damageSpillRadius;
	}

	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissileLine(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				getProjectileArc(), getProjectileArt(), isProjectileHomingEnabled(), getProjectileSpeed(),
				this.damageSpillDistance, this.damageSpillRadius);
	}

	public float getDamageSpillDistance() {
		return this.damageSpillDistance;
	}

	public float getDamageSpillRadius() {
		return this.damageSpillRadius;
	}

	public void setDamageSpillDistance(final float damageSpillDistance) {
		this.damageSpillDistance = damageSpillDistance;
	}

	public void setDamageSpillRadius(final float damageSpillRadius) {
		this.damageSpillRadius = damageSpillRadius;
	}

}
