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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectileInstant;

public class CUnitAttackInstant extends CUnitAttack {
	private String projectileArt;

	public CUnitAttackInstant(final float animationBackswingPoint, final float animationDamagePoint,
			final CAttackType attackType, final float cooldownTime, final int damageBase, final int damageDice,
			final int damageSidesPerDie, final int damageUpgradeAmount, final int range, final float rangeMotionBuffer,
			final boolean showUI, final EnumSet<CTargetType> targetsAllowed, final String weaponSound,
			final CWeaponType weaponType, final String projectileArt) {
		super(animationBackswingPoint, animationDamagePoint, attackType, cooldownTime, damageBase, damageDice,
				damageSidesPerDie, damageUpgradeAmount, range, rangeMotionBuffer, showUI, targetsAllowed, weaponSound,
				weaponType);
		this.projectileArt = projectileArt;
	}

	@Override
	public CUnitAttack copy() {
		return new CUnitAttackInstant(getAnimationBackswingPoint(), getAnimationDamagePoint(), getAttackType(),
				getCooldownTime(), getDamageBase(), getDamageDice(), getDamageSidesPerDie(), getDamageUpgradeAmount(),
				getRange(), getRangeMotionBuffer(), isShowUI(), getTargetsAllowed(), getWeaponSound(), getWeaponType(),
				this.projectileArt);
	}

	public String getProjectileArt() {
		return this.projectileArt;
	}

	public void setProjectileArt(final String projectileArt) {
		this.projectileArt = projectileArt;
	}

	@Override
	public void launch(final CSimulation simulation, final CUnit unit, final AbilityTarget target, final float damage,
			final CUnitAttackListener attackListener) {
		attackListener.onLaunch();
		CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			simulation.createInstantAttackEffect(unit, this, widget);
			CAttackProjectileInstant proj = new CAttackProjectileInstant(widget.getX(), widget.getY(), widget, unit, damage, this, attackListener);
			int i = 0;
			while (!proj.update(simulation) && i < 100) {
				i++;
				widget = proj.getTarget().visit(AbilityTargetWidgetVisitor.INSTANCE);
				simulation.createInstantAttackEffect(unit, this, widget);
			}
		}
	}

	public void doDamage(final CSimulation cSimulation, final CUnit source, final AbilityTarget target,
			final float damage, final float x, final float y,
			final CUnitAttackListener attackListener) {
		final CWidget widget = target.visit(AbilityTargetWidgetVisitor.INSTANCE);
		if (widget != null) {
			CUnitAttackPreDamageListenerDamageModResult modDamage = runPreDamageListeners(cSimulation, source, target, damage);
			float damageDealt = widget.damage(cSimulation, source, true, true, getAttackType(), getWeaponType().getDamageType(), getWeaponSound(), modDamage.computeFinalDamage(), modDamage.getBonusDamage());
			runPostDamageListeners(cSimulation, source, target, damageDealt);
			attackListener.onHit(target, damage);
		}
	}

}
