package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgTargetCheckReceiver;

public class COrderTargetPoint implements COrder {
	private final int abilityHandleId;
	private final int orderId;
	private final AbilityPointTarget target;
	private final boolean queued;

	public COrderTargetPoint(final int abilityHandleId, final int orderId, final AbilityPointTarget target,
			final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.target = target;
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
	public AbilityPointTarget getTarget(final CSimulation game) {
		return this.target;
	}

	@Override
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		final CAbility ability = game.getAbility(this.abilityHandleId);
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final StringMsgTargetCheckReceiver<AbilityPointTarget> targetReceiver = (StringMsgTargetCheckReceiver<AbilityPointTarget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, this.target, targetReceiver);
			if (targetReceiver.getTarget() != null) {
				return ability.begin(game, caster, this.orderId, this.target);
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
