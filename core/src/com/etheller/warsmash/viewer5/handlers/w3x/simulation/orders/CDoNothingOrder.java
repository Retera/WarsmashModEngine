package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class CDoNothingOrder implements COrder {
	private final int orderId;

	public CDoNothingOrder(final int orderId) {
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

	@Override
	public AnimationTokens.PrimaryTag getAnimationName() {
		return AnimationTokens.PrimaryTag.STAND;
	}

}
