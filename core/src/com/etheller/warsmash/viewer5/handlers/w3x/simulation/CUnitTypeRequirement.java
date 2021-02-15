package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import com.etheller.warsmash.util.War3ID;

public class CUnitTypeRequirement {
	private final War3ID requirement;
	private final int requiredLevel;

	public CUnitTypeRequirement(final War3ID requirement, final int requiredLevel) {
		this.requirement = requirement;
		this.requiredLevel = requiredLevel;
	}

	public War3ID getRequirement() {
		return this.requirement;
	}

	public int getRequiredLevel() {
		return this.requiredLevel;
	}
}
