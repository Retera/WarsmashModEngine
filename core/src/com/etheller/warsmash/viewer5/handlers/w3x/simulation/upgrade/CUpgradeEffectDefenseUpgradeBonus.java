package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectDefenseUpgradeBonus implements CUpgradeEffect {

	public CUpgradeEffectDefenseUpgradeBonus() {
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		unit.setPermanentDefenseBonus(unit.getPermanentDefenseBonus() + unit.getUnitType().getDefenseUpgradeBonus());
		unit.notifyAttacksChanged(); // rebuild defense ui for selected unit maybe
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		unit.setPermanentDefenseBonus(unit.getPermanentDefenseBonus() - unit.getUnitType().getDefenseUpgradeBonus());
		unit.notifyAttacksChanged(); // rebuild defense ui for selected unit maybe
	}
}
