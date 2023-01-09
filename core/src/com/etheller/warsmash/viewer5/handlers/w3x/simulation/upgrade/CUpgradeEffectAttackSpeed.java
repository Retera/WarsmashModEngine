package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CUpgradeEffectAttackSpeed implements CUpgradeEffect {
	private final float base;
	private final float mod;

	public CUpgradeEffectAttackSpeed(final float base, final float mod) {
		this.base = base;
		this.mod = mod;
	}

	@Override
	public void apply(final CSimulation simulation, final CUnit unit, final int level) {
		int weaponIndex = 0;
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			final float attackCooldownReduction = StrictMath
					.round(unit.getUnitType().getAttacks().get(weaponIndex).getCooldownTime()
							* Util.levelValue(base, mod, level - 1));
			attack.setCooldownTime(attack.getCooldownTime() - attackCooldownReduction);
			weaponIndex++;
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}

	@Override
	public void unapply(final CSimulation simulation, final CUnit unit, final int level) {
		int weaponIndex = 0;
		for (final CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			final float attackCooldownReduction = StrictMath
					.round(unit.getUnitType().getAttacks().get(weaponIndex).getCooldownTime()
							* Util.levelValue(base, mod, level - 1));
			attack.setCooldownTime(attack.getCooldownTime() + attackCooldownReduction);
			weaponIndex++;
		}
		unit.notifyAttacksChanged(); // rebuild <min> - <max> ui for selected unit maybe
	}
}
