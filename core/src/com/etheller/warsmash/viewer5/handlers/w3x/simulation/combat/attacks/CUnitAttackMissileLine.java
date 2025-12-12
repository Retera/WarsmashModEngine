package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackDamageFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class CUnitAttackMissileLine extends CUnitAttackMissile {
	private float damageSpillDistance;
	private float damageSpillRadius;
	private boolean artillery;

	public CUnitAttackMissileLine(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final EnumSet<SecondaryTag> animationTag, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final float damageSpillDistance,
			final float damageSpillRadius, final boolean isArtillery) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, animationTag, projectileArc, projectileArt, isArtillery ? false : projectileHomingEnabled, projectileSpeed);
		this.damageSpillDistance = damageSpillDistance;
		this.damageSpillRadius = damageSpillRadius;
		this.artillery = isArtillery;
		if (isArtillery) {
			this.damageFlags = new CAttackDamageFlags(true);
			this.damageFlags.setExplode(true);
		}
		initialSettings();
	}

	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissileLine(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				getAnimationTag(), getProjectileArc(), getProjectileArt(), isProjectileHomingEnabled(), getProjectileSpeed(),
				this.damageSpillDistance, this.damageSpillRadius, this.artillery);
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

	public Boolean isArtillery() {
		return this.artillery;
	}

	public CUnitAttackSettings initialSettings() {
		this.attackModifier = super.initialSettings();
		this.attackModifier.addAnimationNames(getAnimationTag());
		if (this.artillery) {
			this.attackModifier.setImpactZ(0f);
			this.attackModifier.setApplyEffectsOnMiss(true);
		}
		return this.attackModifier;
	}

}
