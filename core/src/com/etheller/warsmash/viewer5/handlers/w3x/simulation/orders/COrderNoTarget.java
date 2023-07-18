package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;

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
		if (this.abilityHandleId == 0) {
			return caster.getStopBehavior();
		}
		final CAbility ability = game.getAbility(this.abilityHandleId);
		if (ability == null) {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), "NOTEXTERN: No such ability");
			return caster.pollNextOrderBehavior(game);
		}
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final ExternStringMsgTargetCheckReceiver<Void> targetReceiver = (ExternStringMsgTargetCheckReceiver<Void>) targetCheckReceiver;
			ability.checkCanTargetNoTarget(game, caster, this.orderId, targetReceiver);
			if (targetReceiver.getExternStringKey() == null) {
				caster.fireOrderEvents(game, this);
				return ability.beginNoTarget(game, caster, this.orderId);
			}
			else {
				game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), targetReceiver.getExternStringKey());
				return caster.pollNextOrderBehavior(game);
			}
		}
		else {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
					this.abilityActivationReceiver.getExternStringKey());
			return caster.pollNextOrderBehavior(game);
		}
	}

	@Override
	public AbilityTarget getTarget(final CSimulation game) {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.abilityHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
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
		final COrderNoTarget other = (COrderNoTarget) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		return true;
	}

	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}

}
