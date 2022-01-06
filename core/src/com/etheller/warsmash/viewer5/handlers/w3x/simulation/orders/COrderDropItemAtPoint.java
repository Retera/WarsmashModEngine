package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgTargetCheckReceiver;

public class COrderDropItemAtPoint implements COrder {
	private final int abilityHandleId;
	private final int orderId;
	private final int itemHandleId;
	private final AbilityPointTarget target;
	private final boolean queued;

	public COrderDropItemAtPoint(final int abilityHandleId, final int orderId, final int itemHandleId,
			final AbilityPointTarget target, final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.itemHandleId = itemHandleId;
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
		final CAbilityInventory ability = (CAbilityInventory) game.getAbility(this.abilityHandleId);
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final StringMsgTargetCheckReceiver<AbilityPointTarget> targetReceiver = (StringMsgTargetCheckReceiver<AbilityPointTarget>) targetCheckReceiver;
			final CItem itemToDrop = (CItem) game.getWidget(this.itemHandleId);
			return ability.beginDropItem(game, caster, this.orderId, itemToDrop, this.target);
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
		result = (prime * result) + this.itemHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		result = (prime * result) + ((this.target == null) ? 0 : this.target.hashCode());
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
		final COrderDropItemAtPoint other = (COrderDropItemAtPoint) obj;
		if (this.abilityHandleId != other.abilityHandleId) {
			return false;
		}
		if (this.itemHandleId != other.itemHandleId) {
			return false;
		}
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.queued != other.queued) {
			return false;
		}
		if (this.target == null) {
			if (other.target != null) {
				return false;
			}
		}
		else if (!this.target.equals(other.target)) {
			return false;
		}
		return true;
	}

}
