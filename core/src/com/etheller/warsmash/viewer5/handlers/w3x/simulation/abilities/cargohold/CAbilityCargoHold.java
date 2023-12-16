package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityCargoHold extends AbstractGenericNoIconAbility {
	private int cargoCapacity;
	private final List<CUnit> cargoUnits;
	private float duration;
	private final float castRange;
	private EnumSet<CTargetType> targetsAllowed;

	public CAbilityCargoHold(final int handleId, final War3ID code, final War3ID alias, final int cargoCapacity, final float duration,
			final float castRange, final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, code, alias);
		this.cargoCapacity = cargoCapacity;
		this.cargoUnits = new ArrayList<>();
		this.duration = duration;
		this.castRange = castRange;
		this.targetsAllowed = targetsAllowed;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		unloadAllInstant(game, unit);
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if (orderId == OrderIds.unload) {
			final int targetIndex = cargoUnits.indexOf(target);
			if (targetIndex != -1) {
				dropUnitByIndex(game, caster, targetIndex);
				return false;
			}
		}
		return true;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return caster.pollNextOrderBehavior(game);
	}

	public void dropUnitByIndex(final CSimulation game, final CUnit caster, final int targetIndex) {
		game.unitSoundEffectEvent(caster, getAlias());
		final CUnit firstUnit = removeUnitAtIndex(caster, targetIndex);
		firstUnit.setPointAndCheckUnstuck(caster.getX(), caster.getY(), game);
		firstUnit.setHidden(false);
		firstUnit.setPaused(false);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == OrderIds.unload) {
			if (cargoUnits.contains(target)) {
				receiver.targetOk(target);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_DROP_THIS_ITEM);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit unit) {
		unloadAllInstant(game, unit);
	}

	public int getCargoCapacity() {
		return this.cargoCapacity;
	}

	public float getDuration() {
		return this.duration;
	}

	public float getCastRange() {
		return castRange;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public CUnit getUnit(final int index) {
		return cargoUnits.get(index);
	}

	public void addUnit(final CUnit cargoHoldUnit, final CUnit target) {
		this.cargoUnits.add(target);
	}

	public CUnit removeUnitAtIndex(final CUnit cargoHoldUnit, final int index) {
		return this.cargoUnits.remove(index);
	}

	public boolean isEmpty() {
		return cargoUnits.isEmpty();
	}

	public int getCargoCount() {
		return cargoUnits.size();
	}

	public void setCargoCapacity(final int cargoCapacity) {
		this.cargoCapacity = cargoCapacity;
	}

	public void setDuration(final float duration) {
		this.duration = duration;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public boolean hasCapacity(final int cargoCapacityOfNewUnit) {
		if (cargoCapacityOfNewUnit == 0) {
			return false;
		}
		return (cargoUnits.size() + cargoCapacityOfNewUnit) <= cargoCapacity;
	}

	public void unloadAllInstant(final CSimulation game, final CUnit caster) {
		if (!isEmpty()) {
			game.unitSoundEffectEvent(caster, getAlias());
			final int cargoCount = getCargoCount();
			for (int i = 0; i < cargoCount; i++) {
				final CUnit droppedUnit = removeUnitAtIndex(caster, cargoCount - i - 1);
				droppedUnit.setPointAndCheckUnstuck(caster.getX(), caster.getY(), game);
				droppedUnit.setHidden(false);
				droppedUnit.setPaused(false);
			}
		}
	}
}
