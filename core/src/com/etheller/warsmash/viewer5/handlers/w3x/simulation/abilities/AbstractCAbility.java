package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities;

public abstract class AbstractCAbility implements CAbility {
	private final int handleId;

	public AbstractCAbility(final int handleId) {
		this.handleId = handleId;
	}

	@Override
	public final int getHandleId() {
		return this.handleId;
	}
}
