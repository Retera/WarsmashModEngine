package com.etheller.warsmash.viewer5.handlers.w3x;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public final class TreeBlightingCallback implements CDestructableEnumFunction {
	public static final TreeBlightingCallback INSTANCE = new TreeBlightingCallback();
	private CSimulation simulation;

	public TreeBlightingCallback reset(final CSimulation simulation) {
		this.simulation = simulation;
		return this;
	}

	@Override
	public boolean call(final CDestructable destructable) {
		final boolean checkIsOnBlight = destructable.checkIsOnBlight(this.simulation);
		if (checkIsOnBlight) {
			// no mechanic to unblight so this is intentionally one-way
			destructable.setBlighted(true);
		}
		return false;
	}
}