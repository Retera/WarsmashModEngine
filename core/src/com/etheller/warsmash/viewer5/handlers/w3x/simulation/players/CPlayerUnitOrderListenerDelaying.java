package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.ArrayList;
import java.util.List;

public class CPlayerUnitOrderListenerDelaying implements CPlayerUnitOrderListener {
	private final CPlayerUnitOrderListener delegate;
	private final List<Runnable> actions = new ArrayList<>();

	public CPlayerUnitOrderListenerDelaying(final CPlayerUnitOrderListener delegate) {
		this.delegate = delegate;
	}

	@Override
	public void issueTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final boolean queue) {
		this.actions.add(() -> {
			this.delegate.issueTargetOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, queue);
		});
	}

	@Override
	public void issuePointOrder(final int unitHandleId, final int abilityHandleId, final int orderId, final float x,
			final float y, final boolean queue) {
		this.actions.add(() -> {
			this.delegate.issuePointOrder(unitHandleId, abilityHandleId, orderId, x, y, queue);
		});
	}

	@Override
	public void issueDropItemAtPointOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final float x, final float y, final boolean queue) {
		this.actions.add(() -> {
			this.delegate.issueDropItemAtPointOrder(unitHandleId, abilityHandleId, orderId, targetHandleId, x, y,
					queue);
		});
	}

	@Override
	public void issueDropItemAtTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetItemHandleId, final int targetHeroHandleId, final boolean queue) {
		this.actions.add(() -> {
			this.delegate.issueDropItemAtTargetOrder(unitHandleId, abilityHandleId, orderId, targetItemHandleId,
					targetHeroHandleId, queue);
		});
	}

	@Override
	public void issueImmediateOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final boolean queue) {
		this.actions.add(() -> {
			this.delegate.issueImmediateOrder(unitHandleId, abilityHandleId, orderId, queue);
		});
	}

	@Override
	public void unitCancelTrainingItem(final int unitHandleId, final int cancelIndex) {
		this.actions.add(() -> {
			this.delegate.unitCancelTrainingItem(unitHandleId, cancelIndex);
		});
	}

	public void publishDelayedActions() {
		for (final Runnable action : this.actions) {
			action.run();
		}
		this.actions.clear();
	}

}
