package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgTargetCheckReceiver;

public class COrderTargetWidget implements COrder {
	private final int abilityHandleId;
	private final int orderId;
	private final int targetHandleId;
	private final boolean queued;

	public COrderTargetWidget(final int abilityHandleId, final int orderId, final int targetHandleId,
			final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.targetHandleId = targetHandleId;
		this.queued = queued;
	}

	@Override
	public int getAbilityHandleId() {
		return this.abilityHandleId;
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}

	@Override
	public AbilityTarget getTarget(final CSimulation game) {
		final CUnit target = game.getUnit(this.targetHandleId);
		return target;
	}

	@Override
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		final CAbility ability = game.getAbility(this.abilityHandleId);
		ability.checkCanUse(game, caster, this.orderId, abilityActivationReceiver.reset());
		if (abilityActivationReceiver.isUseOk()) {
			final CUnit target = game.getUnit(this.targetHandleId);
			final StringMsgTargetCheckReceiver<CWidget> targetReceiver = (StringMsgTargetCheckReceiver<CWidget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, target, targetReceiver);
			if (targetReceiver.getTarget() != null) {
				return ability.begin(game, caster, this.orderId, targetReceiver.getTarget());
			}
			else {
				game.getCommandErrorListener().showCommandError(targetReceiver.getMessage());
				return caster.pollNextOrderBehavior(game);
			}
		}
		else {
			game.getCommandErrorListener().showCommandError(this.abilityActivationReceiver.getMessage());
			return caster.pollNextOrderBehavior(game);
		}
	}
}
