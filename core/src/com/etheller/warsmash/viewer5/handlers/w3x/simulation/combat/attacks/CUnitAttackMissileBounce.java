package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class CUnitAttackMissileBounce extends CUnitAttackMissile {
	private float damageLossFactor;
	private int maximumNumberOfTargets;

	public CUnitAttackMissileBounce(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final float damageLossFactor,
			final int maximumNumberOfTargets) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed);
		this.damageLossFactor = damageLossFactor;
		this.maximumNumberOfTargets = maximumNumberOfTargets;
	}

	public float getDamageLossFactor() {
		return this.damageLossFactor;
	}

	public int getMaximumNumberOfTargets() {
		return this.maximumNumberOfTargets;
	}

	public void setDamageLossFactor(final float damageLossFactor) {
		this.damageLossFactor = damageLossFactor;
	}

	public void setMaximumNumberOfTargets(final int maximumNumberOfTargets) {
		this.maximumNumberOfTargets = maximumNumberOfTargets;
	}

}
