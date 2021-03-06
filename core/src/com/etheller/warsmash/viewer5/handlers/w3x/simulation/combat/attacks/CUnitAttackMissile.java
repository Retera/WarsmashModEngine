package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetWidgetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;

public class CUnitAttackMissile extends CUnitAttack {
	private float projectileArc;
	private String projectileArt;
	private boolean projectileHomingEnabled;
	private int projectileSpeed;

	public CUnitAttackMissile(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final float projectileArc, final String projectileArt,
			final boolean projectileHomingEnabled, final int projectileSpeed) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType);
		this.projectileArc = projectileArc;
		this.projectileArt = projectileArt;
		this.projectileHomingEnabled = projectileHomingEnabled;
		this.projectileSpeed = projectileSpeed;
	}

	@Override
	public CUnitAttack copy() {
		return new CUnitAttackMissile(this.getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				this.projectileArc, this.projectileArt, this.projectileHomingEnabled, this.projectileSpeed);
	}

	public float getProjectileArc() {
		return this.projectileArc;
	}

	public String getProjectileArt() {
		return this.projectileArt;
	}

	public boolean isProjectileHomingEnabled() {
		return this.projectileHomingEnabled;
	}

	public int getProjectileSpeed() {
		return this.projectileSpeed;
	}

	public void setProjectileArc(final float projectileArc) {
		this.projectileArc = projectileArc;
	}

	public void setProjectileArt(final String projectileArt) {
		this.projectileArt = projectileArt;
	}

	public void setProjectileHomingEnabled(final boolean projectileHomingEnabled) {
		this.projectileHomingEnabled = projectileHomingEnabled;
	}

	public void setProjectileSpeed(final int projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	@Override
	public void launch(final CSimulation simulation, final CUnit unit, final AbilityTarget target, final float damage,
			final CUnitAttackListener attackListener) {
		attackListener.onLaunch();
		simulation.createProjectile(unit, unit.getX(), unit.getY(), (float) Math.toRadians(unit.getFacing()), this,
				target, damage, 0, attackListener);
	}

	public void doDamage(final CSimulation cSimulation, final CUnit source, final AbilityTarget target,
			final float damage, final float x, final float y, final int bounceIndex,
			final CUnitAttackListener attackListener) {
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			widget.damage(cSimulation, source, getAttackType(), getWeaponSound(), damage);
			attackListener.onHit(target, damage);
		}
	}
}
