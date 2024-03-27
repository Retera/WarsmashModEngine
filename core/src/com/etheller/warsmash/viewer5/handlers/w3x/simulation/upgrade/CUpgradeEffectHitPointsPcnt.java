package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectHitPointsPcnt implements CUpgradeEffect {
	private float base;
	private float mod;

	public CUpgradeEffectHitPointsPcnt(float base, float mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		unit.addMaxLifeRelative(simulation, StrictMath.round(unit.getUnitType().getMaxLife() * Util.levelValue(base,
				mod, level - 1)));
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		unit.addMaxLifeRelative(simulation, -StrictMath.round(unit.getUnitType().getMaxLife() * Util.levelValue(base,
				mod, level - 1)));
	}
}
