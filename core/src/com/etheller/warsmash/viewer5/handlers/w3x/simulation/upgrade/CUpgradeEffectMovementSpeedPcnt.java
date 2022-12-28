package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectMovementSpeedPcnt implements CUpgradeEffect {
	private float base;
	private float mod;

	public CUpgradeEffectMovementSpeedPcnt(float base, float mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		unit.setSpeed(unit.getSpeed()
				+ StrictMath.round(unit.getUnitType().getSpeed() * Util.levelValue(base, mod, level - 1)));
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		unit.setSpeed(unit.getSpeed()
				- StrictMath.round(unit.getUnitType().getSpeed() * Util.levelValue(base, mod, level - 1)));
	}
}
