package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CBuffSlow extends CBuffTimed {

	private final float attackSpeedReductionPercent;
	private final float moveSpeedReductionPercent;

	private int appliedMovementSpeedReduction;

	public CBuffSlow(final int handleId, final War3ID alias, final float duration, final float attackSpeedReductionPercent, final float moveSpeedReductionPercent) {
		super(handleId, alias, alias, duration);
		this.attackSpeedReductionPercent = attackSpeedReductionPercent;
		this.moveSpeedReductionPercent = moveSpeedReductionPercent;
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		super.onDeath(game, cUnit);
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		int speed = unit.getSpeed();
		appliedMovementSpeedReduction = (int)StrictMath.floor(moveSpeedReductionPercent * speed);
		unit.setSpeed(speed - appliedMovementSpeedReduction);

		for(CUnitAttack attack: unit.getUnitSpecificAttacks()) {
			attack.setAttackSpeedBonus(attack.getAttackSpeedBonus() - attackSpeedReductionPercent);
		}
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.setSpeed(unit.getSpeed() + appliedMovementSpeedReduction);
		appliedMovementSpeedReduction = 0;

		for(CUnitAttack attack: unit.getUnitSpecificAttacks()) {
			attack.setAttackSpeedBonus(attack.getAttackSpeedBonus() + attackSpeedReductionPercent);
		}
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

}
