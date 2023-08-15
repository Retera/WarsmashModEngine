package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;

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
	public CWidget getTarget(final CSimulation game) {
		return game.getWidget(this.targetHandleId);
	}

	@Override
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		final CAbility ability = game.getAbility(this.abilityHandleId);
		if (ability == null) {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), "NOTEXTERN: No such ability");
			return caster.pollNextOrderBehavior(game);
		}
		ability.checkCanUse(game, caster, this.orderId, abilityActivationReceiver.reset());
		if (abilityActivationReceiver.isUseOk()) {
			final CWidget target = game.getWidget(this.targetHandleId);
			final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = (ExternStringMsgTargetCheckReceiver<CWidget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, target, targetReceiver.reset());
			if (targetReceiver.getTarget() != null) {
				caster.fireOrderEvents(game, this);
				return ability.begin(game, caster, this.orderId, targetReceiver.getTarget());
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

	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}
}
