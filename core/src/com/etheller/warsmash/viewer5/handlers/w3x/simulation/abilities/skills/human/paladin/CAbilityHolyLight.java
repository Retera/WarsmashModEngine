package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityHolyLight extends CAbilityTargetSpellBase {

	private float healAmount;

	public CAbilityHolyLight(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.holybolt;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		healAmount = worldEditorAbility.getFieldAsFloat(AbilityFields.HolyLight.AMOUNT_HEALED_OR_DAMAGED, level);
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit caster, int orderId, CWidget target, AbilityTargetCheckReceiver<CWidget> receiver) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final boolean undead = targetUnit.getClassifications().contains(CUnitClassification.UNDEAD);
			final boolean ally = targetUnit.isUnitAlly(game.getPlayer(caster.getPlayerIndex()));
			if (undead != ally) {
				if (ally && (targetUnit.getLife() >= targetUnit.getMaximumLife())) {
					receiver.targetCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_HEALTH);
				}
				else {
					receiver.targetOk(targetUnit);
				}
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_FRIENDLY_LIVING_UNITS_OR_ENEMY_UNDEAD_UNITS);
			}
		} else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if(targetUnit != null) {
			if(targetUnit.getClassifications().contains(CUnitClassification.UNDEAD)) {
				targetUnit.damage(simulation, caster, CAttackType.SPELLS, CDamageType.DIVINE, null, healAmount * 0.5f);
			} else {
				targetUnit.heal(simulation, healAmount);
			}
			simulation.createSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET, 0).remove();
		}
		return false;
	}
}
