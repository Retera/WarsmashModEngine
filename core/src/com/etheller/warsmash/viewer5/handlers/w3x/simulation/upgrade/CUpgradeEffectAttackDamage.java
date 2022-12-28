package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CUpgradeEffectAttackDamage implements CUpgradeEffect {
	private int base;
	private int mod;

	public CUpgradeEffectAttackDamage(int base, int mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		for (CUnitAttack attack : unit.getAttacks()) {
			attack.setDamageBase(attack.getDamageBase() + Util.levelValue(base, mod, level - 1));
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		for (CUnitAttack attack : unit.getAttacks()) {
			attack.setDamageBase(attack.getDamageBase() - Util.levelValue(base, mod, level - 1));
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}
}
