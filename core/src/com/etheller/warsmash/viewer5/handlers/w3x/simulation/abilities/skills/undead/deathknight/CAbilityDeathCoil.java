package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

/**
 * Thanks to Spellbound for making this ability (some edits were later made by
 * Retera)
 */
public class CAbilityDeathCoil extends CAbilityTargetSpellBase {
	private float missileSpeed;
	private float healAmount;
	private boolean projectilHomingEnabled;

	public CAbilityDeathCoil(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.deathcoil;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		healAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		missileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);
		projectilHomingEnabled = worldEditorAbility.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, 0);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (validDeathCoilTarget(game, unit, target)) {
			super.innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ENEMY_LIVING_UNITS_OR_FRIENDLY_UNDEAD_UNITS);
		}
	}

	private boolean validDeathCoilTarget(final CSimulation game, final CUnit caster, final CWidget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			if (targetUnit.isUnitType(CUnitTypeJass.UNDEAD)) {
				return targetUnit.isUnitAlly(game.getPlayer(caster.getPlayerIndex()));
			}
			else {
				return !targetUnit.isUnitAlly(game.getPlayer(caster.getPlayerIndex()));
			}
		}
		return false;
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		simulation.createProjectile(caster, getAlias(), caster.getX(), caster.getY(), (float) caster.angleTo(target),
				missileSpeed, projectilHomingEnabled, target, new CAbilityProjectileListener() {
					@Override
					public void onLaunch(final CSimulation game, CProjectile projectile, final AbilityTarget target) {

					}

					@Override
					public void onHit(final CSimulation game, CProjectile projectile, final AbilityTarget abilTarget) {
						final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
						if (targetUnit != null) {
							if (!targetUnit.isUnitType(CUnitTypeJass.UNDEAD)) {
								targetUnit.damage(simulation, caster, false, true, CAttackType.SPELLS, CDamageType.DEATH, null,
										healAmount * .5f);
							}
							else {
								targetUnit.heal(simulation, healAmount);
							}
							simulation.createPersistentSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.SPECIAL, 0).remove();
						}
					}
				});
		return false;
	}
}