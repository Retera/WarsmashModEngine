package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectHitPoints implements CUpgradeEffect {
	private int base;
	private int mod;

	public CUpgradeEffectHitPoints(int base, int mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		float life = unit.getLife();
		int maximumLife = unit.getMaximumLife();
		unit.setMaximumLife(maximumLife + Util.levelValue(base, mod, level - 1));
		unit.setLife(simulation, (life / maximumLife) * unit.getMaximumLife());
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		float life = unit.getLife();
		int maximumLife = unit.getMaximumLife();
		unit.setMaximumLife(maximumLife - Util.levelValue(base, mod, level - 1));
		unit.setLife(simulation, (life / maximumLife) * unit.getMaximumLife());
	}
}
