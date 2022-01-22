package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityItemLifeBonus extends AbstractGenericNoIconAbility {
	private final int lifeBonus;

	public CAbilityItemLifeBonus(final int handleId, final War3ID alias, final int lifeBonus) {
		super(handleId, alias);
		this.lifeBonus = lifeBonus;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		final int oldMaximumLife = unit.getMaximumLife();
		final float oldLife = unit.getLife();
		final int newMaximumLife = Math.round(oldMaximumLife + this.lifeBonus);
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		unit.setMaximumLife(newMaximumLife);
		unit.setLife(game, newLife);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		final int oldMaximumLife = unit.getMaximumLife();
		final float oldLife = unit.getLife();
		final int newMaximumLife = Math.round(oldMaximumLife - this.lifeBonus);
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		unit.setMaximumLife(newMaximumLife);
		unit.setLife(game, newLife);
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

}
