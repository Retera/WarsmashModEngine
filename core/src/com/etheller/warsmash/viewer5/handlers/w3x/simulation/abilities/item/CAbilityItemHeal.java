package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityItemHeal extends CAbilityNoTargetSpellBase {
	private int lifeToRegain;

	public CAbilityItemHeal(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.itemhealingpotion;
	}

	@Override
	protected void innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		if (unit.getLife() >= unit.getMaxLife()) {
			receiver.activationCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_HEALTH);
		}
		else {
			super.innerCheckCanUseSpell(game, unit, orderId, receiver);
		}
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.lifeToRegain = worldEditorAbility.getFieldAsInteger(AbilityFields.ItemHealingLesser.HIT_POINTS_GAINED, level);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		caster.heal(simulation, this.lifeToRegain);
		simulation.createSpellEffectOnUnit(caster, getAlias(), CEffectType.TARGET);
		return false;
	}
}
