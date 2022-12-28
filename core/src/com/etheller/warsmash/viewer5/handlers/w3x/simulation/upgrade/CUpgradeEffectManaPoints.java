package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectManaPoints implements CUpgradeEffect {
	private int base;
	private int mod;

	public CUpgradeEffectManaPoints(int base, int mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		float mana = unit.getMana();
		int maximumMana = unit.getMaximumMana();
		unit.setMaximumMana(maximumMana + Util.levelValue(base, mod, level - 1));
		unit.setMana((mana / maximumMana) * unit.getMaximumMana());
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		float mana = unit.getMana();
		int maximumMana = unit.getMaximumMana();
		unit.setMaximumMana(maximumMana - Util.levelValue(base, mod, level - 1));
		unit.setMana((mana / maximumMana) * unit.getMaximumMana());
	}
}
