package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.util.SubscriberSetNotifier;

public interface CUnitStateListener {
	void lifeChanged(); // hp (current) changes

	void ordersChanged(int abilityHandleId, int orderId);

	void queueChanged();

	void rallyPointChanged();

	public static final class CUnitStateNotifier extends SubscriberSetNotifier<CUnitStateListener>
			implements CUnitStateListener {
		@Override
		public void lifeChanged() {
			for (final CUnitStateListener listener : set) {
				listener.lifeChanged();
			}
		}

		@Override
		public void ordersChanged(final int abilityHandleId, final int orderId) {
			for (final CUnitStateListener listener : set) {
				listener.ordersChanged(abilityHandleId, orderId);
			}
		}

		@Override
		public void queueChanged() {
			for (final CUnitStateListener listener : set) {
				listener.queueChanged();
			}
		}

		@Override
		public void rallyPointChanged() {
			for (final CUnitStateListener listener : set) {
				listener.rallyPointChanged();
			}
		}
	}
}
