package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CUpgradeEffectAttackDice implements CUpgradeEffect {
	private final int base;
	private final int mod;

	public CUpgradeEffectAttackDice(final int base, final int mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(final CSimulation simulation, final CUnit unit, final int level) {
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setDamageDice(attack.getDamageDice() + Util.levelValue(base, mod, level - 1));
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}

	@Override
	public void unapply(final CSimulation simulation, final CUnit unit, final int level) {
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			attack.setDamageDice(attack.getDamageDice() - Util.levelValue(base, mod, level - 1));
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}
}
