package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;

public class CUnitAppliedUpgrade {
	private int level;
	private CUpgradeType type;

	public CUnitAppliedUpgrade(CUpgradeType type, int level) {
		this.type = type;
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public CUpgradeType getType() {
		return type;
	}

	public void setType(CUpgradeType type) {
		this.type = type;
	}

}
