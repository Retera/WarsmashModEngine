package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class CUnitAttackMissileBounce extends CUnitAttackMissile {
	private float damageLossFactor;
	private int maximumNumberOfTargets;
	private final int areaOfEffectFullDamage;
	private final EnumSet<CTargetType> areaOfEffectTargets;

	public CUnitAttackMissileBounce(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed, final float damageLossFactor,
			final int maximumNumberOfTargets, final int areaOfEffectFullDamage,
			final EnumSet<CTargetType> areaOfEffectTargets) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType, projectileArc, projectileArt, projectileHomingEnabled, projectileSpeed);
		this.damageLossFactor = damageLossFactor;
		this.maximumNumberOfTargets = maximumNumberOfTargets;
		this.areaOfEffectFullDamage = areaOfEffectFullDamage;
		this.areaOfEffectTargets = areaOfEffectTargets;
	}

	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissileBounce(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				getProjectileArc(), getProjectileArt(), isProjectileHomingEnabled(), getProjectileSpeed(),
				this.damageLossFactor, this.maximumNumberOfTargets, this.areaOfEffectFullDamage,
				this.areaOfEffectTargets);
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

	@Override
	public void doDamage(final CSimulation cSimulation, final CUnit source, final AbilityTarget target,
			final float damage, final float x, final float y, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		super.doDamage(cSimulation, source, target, damage, x, y, bounceIndex, attackListener);
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			final int nextBounceIndex = bounceIndex + 1;
			if (nextBounceIndex != this.maximumNumberOfTargets) {
				BounceMissileConsumer.INSTANCE.nextBounce(cSimulation, source, widget, this, x, y, damage,
						nextBounceIndex, attackListener);
			}
		}
	}

	private static final class BounceMissileConsumer implements CUnitEnumFunction {
		private static final BounceMissileConsumer INSTANCE = new BounceMissileConsumer();
		private final Rectangle rect = new Rectangle();
		private CUnitAttackMissileBounce attack;
		private CSimulation simulation;
		private CUnit source;
		private CWidget target;
		private float x;
		private float y;
		private float damage;
		private int bounceIndex;
		private CUnitAttackListener attackListener;
		private boolean launched = false;

		public void nextBounce(final CSimulation simulation, final CUnit source, final CWidget target,
				final CUnitAttackMissileBounce attack, final float x, final float y, final float damage,
				final int bounceIndex, final CUnitAttackListener attackListener) {
			this.simulation = simulation;
			this.source = source;
			this.target = target;
			this.attack = attack;
			this.x = x;
			this.y = y;
			this.damage = damage;
			this.bounceIndex = bounceIndex;
			this.attackListener = attackListener;
			this.launched = false;
			final float doubleMaxArea = attack.areaOfEffectFullDamage
					+ (this.simulation.getGameplayConstants().getCloseEnoughRange() * 2);
			final float maxArea = doubleMaxArea / 2;
			this.rect.set(x - maxArea, y - maxArea, doubleMaxArea, doubleMaxArea);
			simulation.getWorldCollision().enumUnitsInRect(this.rect, this);

		}

		@Override
		public boolean call(final CUnit enumUnit) {
			if (enumUnit == this.target) {
				return false;
			}
			if (enumUnit.canBeTargetedBy(this.simulation, this.source, this.attack.areaOfEffectTargets)) {
				if (this.launched) {
					throw new IllegalStateException("already launched");
				}
				final float dx = enumUnit.getX() - this.x;
				final float dy = enumUnit.getY() - this.y;
				final float angle = (float) Math.atan2(dy, dx);
				this.simulation.createProjectile(this.source, this.x, this.y, angle, this.attack, enumUnit,
						this.damage * (1.0f - this.attack.damageLossFactor), this.bounceIndex, this.attackListener);
				this.launched = true;
				return true;
			}
			return false;
		}
	}
}
