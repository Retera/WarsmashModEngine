package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking;

import java.util.EnumSet;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetUnitVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityThunderBolt extends CAbilityTargetSpellBase {

	private float damage;
	private float projectileSpeed;
	private boolean projectileHomingEnabled;
	private War3ID buffId;

	public CAbilityThunderBolt(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.thunderbolt;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		damage = worldEditorAbility.getFieldAsFloat(AbilityFields.StormBolt.DAMAGE, level);
		projectileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, level);
		projectileHomingEnabled = worldEditorAbility.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, level);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			simulation.createProjectile(targetUnit, getAlias(), caster.getX(), caster.getY(),
					(float) caster.angleTo(targetUnit), projectileSpeed, projectileHomingEnabled, targetUnit,
					new CAbilityProjectileListener() {
						@Override
						public void onLaunch(CSimulation game, AbilityTarget target) {

						}

						@Override
						public void onHit(CSimulation game, AbilityTarget target) {
							final CUnit unitTarget = target.visit(AbilityTargetUnitVisitor.INSTANCE);
							if (unitTarget != null) {
								unitTarget.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.LIGHTNING,
										CWeaponSoundTypeJass.WHOKNOWS.name(), damage);
								if (!unitTarget.isDead()) {
									unitTarget.add(game, new CBuffStun(game.getHandleIdAllocator().createId(), getBuffId(),
											getDurationForTarget(unitTarget)));
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

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(float projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	public boolean isProjectileHomingEnabled() {
		return projectileHomingEnabled;
	}

	public void setProjectileHomingEnabled(boolean projectileHomingEnabled) {
		this.projectileHomingEnabled = projectileHomingEnabled;
	}

	public War3ID getBuffId() {
		return buffId;
	}

	public void setBuffId(War3ID buffId) {
		this.buffId = buffId;
	}
}