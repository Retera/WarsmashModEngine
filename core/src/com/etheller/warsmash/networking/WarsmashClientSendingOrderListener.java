package com.etheller.warsmash.networking;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;

public class WarsmashClientSendingOrderListener implements CPlayerUnitOrderListener {
	private final WarsmashClientWriter writer;

	public WarsmashClientSendingOrderListener(final WarsmashClientWriter writer) {
		this.writer = writer;
	}

	@Override
	public void issueTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final boolean queue) {
		this.writer.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
		this.writer.send();
	}

	@Override
	public void issuePointOrder(final int unitHandleId, final int abilityHandleId, final int orderId, final float x,
			final float y, final boolean queue) {
		this.writer.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
		this.writer.send();
	}

	@Override
	public void issueDropItemAtPointOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final float x, final float y, final boolean queue) {
		this.writer.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, x, y, queue);
		this.writer.send();
	}

	@Override
	public void issueDropItemAtTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetItemHandleId, final int targetHeroHandleId, final boolean queue) {
		this.writer.issueDropItemAtTargetOrder(unitHandleId, abilityHandleId, orderId, targetItemHandleId,
				targetHeroHandleId, queue);
		this.writer.send();
	}

	@Override
	public void issueImmediateOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final boolean queue) {
		this.writer.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
		this.writer.send();
	}

	@Override
	public void unitCancelTrainingItem(final int unitHandleId, final int cancelIndex) {
		this.writer.unitCancelTrainingItem(unitHandleId, cancelIndex);
		this.writer.send();
	}

}
