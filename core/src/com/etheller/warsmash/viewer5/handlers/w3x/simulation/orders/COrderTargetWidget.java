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
		final CWidget target = game.getWidget(this.targetHandleId);
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
			final CWidget target = game.getWidget(this.targetHandleId);
			final StringMsgTargetCheckReceiver<CWidget> targetReceiver = (StringMsgTargetCheckReceiver<CWidget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, target, targetReceiver.reset());
			if (targetReceiver.getTarget() != null) {
				return ability.begin(game, caster, this.orderId, targetReceiver.getTarget());
			}
			else {
				game.getCommandErrorListener().showCommandError(caster.getPlayerIndex(), targetReceiver.getMessage());
				return caster.pollNextOrderBehavior(game);
			}
		}
		else {
			game.getCommandErrorListener().showCommandError(caster.getPlayerIndex(),
					this.abilityActivationReceiver.getMessage());
			return caster.pollNextOrderBehavior(game);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.abilityHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		result = (prime * result) + this.targetHandleId;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final COrderTargetWidget other = (COrderTargetWidget) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		if (this.targetHandleId != other.targetHandleId) {
			return false;
		}
		return true;
	}
}
