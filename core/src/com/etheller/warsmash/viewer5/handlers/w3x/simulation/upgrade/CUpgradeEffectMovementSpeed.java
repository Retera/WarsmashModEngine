package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectMovementSpeed implements CUpgradeEffect {
	private int base;
	private int mod;

	public CUpgradeEffectMovementSpeed(int base, int mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		unit.setSpeed(unit.getSpeed() + Util.levelValue(base, mod, level - 1));
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		unit.setSpeed(unit.getSpeed() - Util.levelValue(base, mod, level - 1));
	}
}
