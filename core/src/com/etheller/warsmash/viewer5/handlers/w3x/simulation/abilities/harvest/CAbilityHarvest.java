package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityHarvest extends AbstractGenericSingleIconActiveAbility {
	private int carriedResourceAmount;
	private ResourceType carriedResourceType;

	public CAbilityHarvest(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.harvest;
	}

	@Override
	public boolean isToggleOn() {
		return this.carriedResourceAmount > 0;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			if (targetUnit.getGold() > 0) {
				receiver.targetOk(target);
			}
			else {
				receiver.mustTargetResources();
			}
		}
		else {
			receiver.mustTargetResources();
		}
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {

	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {

	}

}
