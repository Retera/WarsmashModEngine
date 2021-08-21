package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
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

	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissileSplash(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				getProjectileArc(), getProjectileArt(), isProjectileHomingEnabled(), getProjectileSpeed(),
				this.areaOfEffectFullDamage, this.areaOfEffectMediumDamage, this.areaOfEffectSmallDamage,
				this.areaOfEffectTargets, this.damageFactorMedium, this.damageFactorSmall);
	}

	@Override
	public int getRange() {
		return super.getRange();
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

	@Override
	public void doDamage(final CSimulation cSimulation, final CUnit source, final AbilityTarget target,
			final float damage, final float x, final float y, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		SplashDamageConsumer.INSTANCE.doDamage(cSimulation, source, target, this, x, y, damage, attackListener);
		if ((getWeaponType() != CWeaponType.ARTILLERY) && !SplashDamageConsumer.INSTANCE.hitTarget) {
			float originalTargetDamage = damage;
			if (Math.abs(this.damageFactorSmall) > 0.0001) {
				originalTargetDamage *= this.damageFactorSmall;
			}
			super.doDamage(cSimulation, source, target, originalTargetDamage, x, y, bounceIndex, attackListener);
		}
	}

	private static final class SplashDamageConsumer implements CUnitEnumFunction, CDestructableEnumFunction {
		private static final SplashDamageConsumer INSTANCE = new SplashDamageConsumer();
		private final Rectangle rect = new Rectangle();
		private CUnitAttackMissileSplash attack;
		private CSimulation simulation;
		private CUnit source;
		private AbilityTarget target;
		private float x;
		private float y;
		private float damage;
		private CUnitAttackListener attackListener;
		private boolean hitTarget;

		public void doDamage(final CSimulation simulation, final CUnit source, final AbilityTarget target,
				final CUnitAttackMissileSplash attack, final float x, final float y, final float damage,
				final CUnitAttackListener attackListener) {
			this.simulation = simulation;
			this.source = source;
			this.target = target;
			this.attack = attack;
			this.x = x;
			this.y = y;
			this.damage = damage;
			this.attackListener = attackListener;
			this.hitTarget = false;
			final float doubleMaxArea = (attack.areaOfEffectSmallDamage) * 2;
			final float maxArea = doubleMaxArea / 2;
			this.rect.set(x - maxArea, y - maxArea, doubleMaxArea, doubleMaxArea);
			simulation.getWorldCollision().enumUnitsInRect(this.rect, this);
			simulation.getWorldCollision().enumDestructablesInRect(this.rect, this);
		}

		@Override
		public boolean call(final CUnit enumUnit) {
			if (enumUnit.canBeTargetedBy(this.simulation, this.source, this.attack.areaOfEffectTargets)) {
				final double distance = enumUnit.distance(this.x, this.y);
				if (distance <= (this.attack.areaOfEffectFullDamage)) {
					enumUnit.damage(this.simulation, this.source, this.attack.getAttackType(),
							this.attack.getWeaponSound(), this.damage);
					this.attackListener.onHit(enumUnit, this.damage);
				} else if (distance <= (this.attack.areaOfEffectMediumDamage)) {
					enumUnit.damage(this.simulation, this.source, this.attack.getAttackType(),
							this.attack.getWeaponSound(), this.damage * this.attack.damageFactorMedium);
					this.attackListener.onHit(enumUnit, this.damage);
				} else if (distance <= (this.attack.areaOfEffectSmallDamage)) {
					enumUnit.damage(this.simulation, this.source, this.attack.getAttackType(),
							this.attack.getWeaponSound(), this.damage * this.attack.damageFactorSmall);
					this.attackListener.onHit(enumUnit, this.damage);
				}
				if (enumUnit == this.target) {
					this.hitTarget = true;
				}
			}
			return false;
		}

		@Override
		public boolean call(final CDestructable enumDestructable) {
			if (enumDestructable.canBeTargetedBy(this.simulation, this.source, this.attack.areaOfEffectTargets)) {
				final double distance = enumDestructable.distance(this.x, this.y);
				if (distance <= (this.attack.areaOfEffectFullDamage)) {
					enumDestructable.damage(this.simulation, this.source, this.attack.getAttackType(),
							this.attack.getWeaponSound(), this.damage);
					this.attackListener.onHit(enumDestructable, this.damage);
				} else if (distance <= (this.attack.areaOfEffectMediumDamage)) {
					enumDestructable.damage(this.simulation, this.source, this.attack.getAttackType(),
							this.attack.getWeaponSound(), this.damage * this.attack.damageFactorMedium);
					this.attackListener.onHit(enumDestructable, this.damage);
				} else if (distance <= (this.attack.areaOfEffectSmallDamage)) {
					enumDestructable.damage(this.simulation, this.source, this.attack.getAttackType(),
							this.attack.getWeaponSound(), this.damage * this.attack.damageFactorSmall);
					this.attackListener.onHit(enumDestructable, this.damage);
				}
				if (enumDestructable == this.target) {
					this.hitTarget = true;
				}
			}
			return false;
		}
	}
}
