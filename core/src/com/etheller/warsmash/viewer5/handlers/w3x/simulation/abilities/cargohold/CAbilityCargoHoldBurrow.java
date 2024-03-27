package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CAbilityCargoHoldBurrow extends CAbilityCargoHold {
	private float[] originalBaseAttackRates;

	public CAbilityCargoHoldBurrow(final int handleId, final War3ID code, final War3ID alias, final int cargoCapacity,
			final float duration, final float castRange, final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, code, alias, cargoCapacity, duration, castRange, targetsAllowed);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// NOTE: this might have weird interactions with heroes, which
		// also setup unit specific attack objects in memory
		final List<CUnitAttack> unitSpecificAttacks = unit.getUnitSpecificAttacks();
		final int originalAttackCount = unitSpecificAttacks.size();
		originalBaseAttackRates = new float[originalAttackCount];
		for (int i = 0; i < originalAttackCount; i++) {
			final CUnitAttack originalAttack = unitSpecificAttacks.get(i);
			originalBaseAttackRates[i] = originalAttack.getCooldownTime();
		}
		unit.setDisableAttacks(true);
		super.onAdd(game, unit);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		unit.setDisableAttacks(false);
		super.onRemove(game, unit);
	}

	@Override
	public void addUnit(final CUnit cargoHoldUnit, final CUnit target) {
		final boolean wasEmpty = isEmpty();
		super.addUnit(cargoHoldUnit, target);
		if (wasEmpty) {
			cargoHoldUnit.setDisableAttacks(false);
		}
		updateAttackCooldowns(cargoHoldUnit);
	}

	@Override
	public CUnit removeUnitAtIndex(final CUnit cargoHoldUnit, final int index) {
		final CUnit removedUnit = super.removeUnitAtIndex(cargoHoldUnit, index);
		if (isEmpty()) {
			cargoHoldUnit.setDisableAttacks(true);
		}
		else {
			updateAttackCooldowns(cargoHoldUnit);
		}
		return removedUnit;
	}

	private void updateAttackCooldowns(final CUnit cargoHoldUnit) {
		final List<CUnitAttack> unitSpecificAttacks = cargoHoldUnit.getUnitSpecificAttacks();
		for (int i = 0, l = unitSpecificAttacks.size(); i < l; i++) {
			unitSpecificAttacks.get(i)
					.setCooldownTime(originalBaseAttackRates[i] / (float) Math.pow(2, getCargoCount()));
		}

	}
}
