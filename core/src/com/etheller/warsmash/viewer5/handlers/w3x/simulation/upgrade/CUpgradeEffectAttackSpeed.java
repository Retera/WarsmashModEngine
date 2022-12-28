package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CUpgradeEffectAttackSpeed implements CUpgradeEffect {
	private float base;
	private float mod;

	public CUpgradeEffectAttackSpeed(float base, float mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
		int weaponIndex = 0;
		for (CUnitAttack attack : unit.getAttacks()) {
			float attackCooldownReduction = StrictMath
					.round(unit.getUnitType().getAttacks().get(weaponIndex).getCooldownTime()
							* Util.levelValue(base, mod, level - 1));
			attack.setCooldownTime(attack.getCooldownTime() - attackCooldownReduction);
			weaponIndex++;
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
		int weaponIndex = 0;
		for (CUnitAttack attack : unit.getAttacks()) {
			float attackCooldownReduction = StrictMath
					.round(unit.getUnitType().getAttacks().get(weaponIndex).getCooldownTime()
							* Util.levelValue(base, mod, level - 1));
			attack.setCooldownTime(attack.getCooldownTime() + attackCooldownReduction);
			weaponIndex++;
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}
}
