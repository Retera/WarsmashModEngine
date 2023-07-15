package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.warlord;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffAuraBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CBuffCommand extends CBuffAuraBase {

	private final int attackDamageIncrease;
	private final boolean meleeBonus;
	private final boolean rangedBonus;

	public CBuffCommand(int handleId, War3ID alias, int attackDamageIncrease, boolean meleeBonus,
						boolean rangedBonus) {
		super(handleId, alias);
		this.attackDamageIncrease = attackDamageIncrease;
		this.meleeBonus = meleeBonus;
		this.rangedBonus = rangedBonus;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		for (CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			if (attack.getWeaponType() == CWeaponType.NORMAL) {
				if (this.meleeBonus) {
					attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() + attackDamageIncrease);
				}

			}
			else {
				if (this.rangedBonus) {
					attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() + attackDamageIncrease);
				}
			}
		}
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		for (CUnitAttack attack : unit.getUnitSpecificAttacks()) {
			if (attack.getWeaponType() == CWeaponType.NORMAL) {
				if (this.meleeBonus) {
					attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() - attackDamageIncrease);
				}

			}
			else {
				if (this.rangedBonus) {
					attack.setTemporaryDamageBonus(attack.getTemporaryDamageBonus() - attackDamageIncrease);
				}
			}
		}
	}
}
