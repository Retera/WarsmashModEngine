package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;

public class COrderDropItemAtTargetWidget implements COrder {
	private final int abilityHandleId;
	private final int orderId;
	private final int itemHandleId;
	private final int targetHeroHandleId;
	private final boolean queued;

	public COrderDropItemAtTargetWidget(final int abilityHandleId, final int orderId, final int itemHandleId,
			final int targetHeroHandleId, final boolean queued) {
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.itemHandleId = itemHandleId;
		this.targetHeroHandleId = targetHeroHandleId;
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
		final CWidget target = game.getWidget(this.targetHeroHandleId);
		return target;
	}

	@Override
	public boolean isQueued() {
		return this.queued;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		final CAbilityInventory ability = (CAbilityInventory) game.getAbility(this.abilityHandleId);
		if (ability == null) {
			game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(), "NOTEXTERN: No such ability");
			return caster.pollNextOrderBehavior(game);
		}
		ability.checkCanUse(game, caster, this.orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final CItem itemToDrop = (CItem) game.getWidget(this.itemHandleId);
			final CUnit targetHero = (CUnit) game.getWidget(this.targetHeroHandleId);
			final ExternStringMsgTargetCheckReceiver<CWidget> targetReceiver = (ExternStringMsgTargetCheckReceiver<CWidget>) targetCheckReceiver;
			ability.checkCanTarget(game, caster, this.orderId, targetHero, targetReceiver.reset());
			if (targetReceiver.getTarget() != null) {
				return ability.beginDropItem(game, caster, this.orderId, itemToDrop, targetHero);
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
		result = (prime * result) + this.itemHandleId;
		result = (prime * result) + this.orderId;
		result = (prime * result) + (this.queued ? 1231 : 1237);
		result = (prime * result) + this.targetHeroHandleId;
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
		final COrderDropItemAtTargetWidget other = (COrderDropItemAtTargetWidget) obj;
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
		if (this.targetHeroHandleId != other.targetHeroHandleId) {
			return false;
		}
		return true;
	}

	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		// TODO Auto-generated method stub
	}

}
