package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgTargetCheckReceiver;

public class COrderNoTarget implements COrder {
	private final int abilityHandleId;
	private final int orderId;
	private final boolean queued;

	public COrderNoTarget(final int abilityHandleId, final int orderId, final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
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
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		final CAbility ability = game.getAbility(this.abilityHandleId);
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final StringMsgTargetCheckReceiver<Void> targetReceiver = (StringMsgTargetCheckReceiver<Void>) targetCheckReceiver;
			ability.checkCanTargetNoTarget(game, caster, this.orderId, targetReceiver);
			if (targetReceiver.getMessage() == null) {
				return ability.beginNoTarget(game, caster, this.orderId);
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

	@Override
	public AbilityTarget getTarget(final CSimulation game) {
		return null;
	}

}
