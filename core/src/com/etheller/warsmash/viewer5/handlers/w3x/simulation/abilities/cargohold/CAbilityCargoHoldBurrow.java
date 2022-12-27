package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CAbilityCargoHoldBurrow extends CAbilityCargoHold {
	private float[] originalBaseAttackRates;

	public CAbilityCargoHoldBurrow(int handleId, War3ID alias, int cargoCapacity, float duration, float castRange,
			EnumSet<CTargetType> targetsAllowed) {
		super(handleId, alias, cargoCapacity, duration, castRange, targetsAllowed);
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		// NOTE: this might have weird interactions with heroes, which
		// also setup unit specific attack objects in memory
		List<CUnitAttack> originalAttacks = unit.getAttacks();
		List<CUnitAttack> unitSpecificAttacks = new ArrayList<>();
		int originalAttackCount = originalAttacks.size();
		originalBaseAttackRates = new float[originalAttackCount];
		for (int i = 0; i < originalAttackCount; i++) {
			CUnitAttack originalAttack = originalAttacks.get(i);
			unitSpecificAttacks.add(originalAttack.copy());
			originalBaseAttackRates[i] = originalAttack.getCooldownTime();
		}
		unit.setUnitSpecificAttacks(unitSpecificAttacks);
		unit.setDisableAttacks(true);
		super.onAdd(game, unit);
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		unit.setDisableAttacks(false);
		super.onRemove(game, unit);
	}

	@Override
	public void addUnit(CUnit cargoHoldUnit, CUnit target) {
		boolean wasEmpty = isEmpty();
		super.addUnit(cargoHoldUnit, target);
		if (wasEmpty) {
			cargoHoldUnit.setDisableAttacks(false);
		}
		updateAttackCooldowns(cargoHoldUnit);
	}

	@Override
	public CUnit removeUnitAtIndex(CUnit cargoHoldUnit, int index) {
		CUnit removedUnit = super.removeUnitAtIndex(cargoHoldUnit, index);
		if (isEmpty()) {
			cargoHoldUnit.setDisableAttacks(true);
		}
		else {
			updateAttackCooldowns(cargoHoldUnit);
		}
		return removedUnit;
	}

	private void updateAttackCooldowns(CUnit cargoHoldUnit) {
		List<CUnitAttack> unitSpecificAttacks = cargoHoldUnit.getUnitSpecificAttacks();
		for (int i = 0, l = unitSpecificAttacks.size(); i < l; i++) {
			unitSpecificAttacks.get(i)
					.setCooldownTime(originalBaseAttackRates[i] / (float) Math.pow(2, getCargoCount()));
		}

	}
}
