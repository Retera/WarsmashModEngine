package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetUnitVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;

public class CAbilityThunderBolt extends CAbilityTargetSpellBase {

	private float damage;
	private float projectileSpeed;
	private boolean projectileHomingEnabled;
	private War3ID buffId;

	public CAbilityThunderBolt(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.thunderbolt;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		damage = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		projectileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);
		projectileHomingEnabled = worldEditorAbility.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			simulation.createProjectile(targetUnit, getAlias(), caster.getX(), caster.getY(),
					(float) caster.angleTo(targetUnit), projectileSpeed, projectileHomingEnabled, targetUnit,
					new CAbilityProjectileListener() {
						@Override
						public void onLaunch(final CSimulation game, CProjectile projectile, final AbilityTarget target) {

						}

						@Override
						public void onHit(final CSimulation game, CProjectile projectile, final AbilityTarget target) {
							final CUnit unitTarget = target.visit(AbilityTargetUnitVisitor.INSTANCE);
							if (unitTarget != null) {
								unitTarget.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
										CWeaponSoundTypeJass.WHOKNOWS.name(), damage);
								if (!unitTarget.isDead()) {
									unitTarget.add(game, new CBuffStun(game.getHandleIdAllocator().createId(),
											getBuffId(), getDurationForTarget(unitTarget)));
								}
							}
						}
					});
		}
		return false;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(final float damage) {
		this.damage = damage;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(final float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	public boolean isProjectileHomingEnabled() {
		return projectileHomingEnabled;
	}

	public void setProjectileHomingEnabled(final boolean projectileHomingEnabled) {
		this.projectileHomingEnabled = projectileHomingEnabled;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}
}