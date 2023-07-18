package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

/**
 * Thanks to Spellbound for making this ability (some edits were later made by Retera)
 */
public class CAbilityDeathCoil extends CAbilityTargetSpellBase {
	private float missileSpeed;
	private float healAmount;
	private boolean projectilHomingEnabled;

	public CAbilityDeathCoil(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.deathcoil;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		healAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.DeathCoil.AMOUNT_HEALED_OR_DAMAGED, level);
		missileSpeed = worldEditorAbility.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, level);
		projectilHomingEnabled = worldEditorAbility.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, level);
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
									   AbilityTargetCheckReceiver<CWidget> receiver) {
		if (validDeathCoilTarget(game, unit, target)) {
			super.innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ENEMY_LIVING_UNITS_OR_FRIENDLY_UNDEAD_UNITS);
		}
	}

	private boolean validDeathCoilTarget(CSimulation game, CUnit caster, CWidget target) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
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
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		simulation.createProjectile(caster, getAlias(), caster.getX(), caster.getY(),
				(float) caster.angleTo(target), missileSpeed, projectilHomingEnabled, target, new CAbilityProjectileListener() {
					@Override
					public void onLaunch(CSimulation game, AbilityTarget target) {

					}

					@Override
					public void onHit(CSimulation game, AbilityTarget abilTarget) {
						CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
						if (targetUnit != null) {
							if (!targetUnit.isUnitType(CUnitTypeJass.UNDEAD)) {
								targetUnit.damage(simulation, caster, CAttackType.SPELLS, CDamageType.DEATH, null,
										healAmount * .5f);
							}
							else {
								targetUnit.heal(simulation, healAmount);
							}
							simulation.createSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.SPECIAL, 0).remove();
						}
					}
				});
		return false;
	}
}