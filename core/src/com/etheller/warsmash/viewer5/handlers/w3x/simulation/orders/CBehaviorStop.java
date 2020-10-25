package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class CBehaviorStop implements CBehavior {
	private final int orderId;

	public CBehaviorStop(final int orderId) {
		this.orderId = orderId;
	}

	@Override
	public boolean update(final CSimulation game) {
		return true;
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}

}
