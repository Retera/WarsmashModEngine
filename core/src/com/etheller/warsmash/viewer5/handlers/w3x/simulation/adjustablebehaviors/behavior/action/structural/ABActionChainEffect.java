package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs.ABNearestUnitComparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs.ABUnitComparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionChainEffect implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();
	private List<ABAction> actions;
	private ABBooleanCallback condition;
	private ABIntegerCallback sort;

	private ABUnitCallback target;

	private ABBooleanCallback lightningFromCaster;
	private ABIDCallback lightningId;
	private ABIntegerCallback lightningIndex;

	private ABFloatCallback range;
	private ABIntegerCallback bounces;
	private ABFloatCallback bounceDelay;

	private ABBooleanCallback allowMultipleBouncesPerUnit;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final CUnit originUnitTarget = this.target.callback(caster, localStore, castId);
		boolean multiBounce = localStore.game.getGameplayConstants().isAllowMultiBounce();
		if (allowMultipleBouncesPerUnit != null) {
			multiBounce = allowMultipleBouncesPerUnit.callback(caster, localStore, castId);
		}
		Set<CUnit> hitUnits = null;
		if (!multiBounce) {
			hitUnits = new HashSet<>();
			hitUnits.add(originUnitTarget);
		}

		if (lightningFromCaster == null || lightningFromCaster.callback(caster, localStore, castId)) {
			if (lightningId != null) {
				int lidx = 0;
				if (lightningIndex != null) {
					lidx = lightningIndex.callback(caster, localStore, castId);
				}
				localStore.game.createAbilityLightning(caster, lightningId.callback(caster, localStore, castId), lidx,
						originUnitTarget, 2f);
			}
		}

		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CHAINUNIT, castId), originUnitTarget);
		for (ABAction iterationAction : actions) {
			iterationAction.runAction(caster, localStore, castId);
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CHAINUNIT, castId));

		startPerformJump(caster, localStore, castId, originUnitTarget, multiBounce, hitUnits,
				bounces.callback(caster, localStore, castId));

	}

	private void startPerformJump(final CUnit caster, final ABLocalDataStore localStore, final int castId,
			final CUnit originUnitTarget, final boolean multiBounce, final Set<CUnit> hitUnits,
			final int remainingJumps) {
		if (remainingJumps <= 0) {
			return;
		}
		float delay = 0;
		if (bounceDelay != null) {
			delay = bounceDelay.callback(caster, localStore, castId);
		}
		if (delay > 0) {
			CTimer runner = new CTimer() {
				@Override
				public void onFire(CSimulation simulation) {
					performJump(caster, localStore, castId, originUnitTarget, multiBounce, hitUnits,
							remainingJumps - 1);
				}
			};
			runner.setTimeoutTime(delay);
			runner.start(localStore.game);
		} else {
			performJump(caster, localStore, castId, originUnitTarget, multiBounce, hitUnits, remainingJumps - 1);
		}
	}

	private void performJump(final CUnit caster, final ABLocalDataStore localStore, final int castId,
			final CUnit originUnitTarget, final boolean multiBounce, final Set<CUnit> hitUnits,
			final int remainingJumps) {
		if (originUnitTarget == null) {
			return;
		}

		final Float rangeVal = this.range.callback(caster, localStore, castId);

		List<CUnit> foundUnits = new ArrayList<>();
		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		localStore.game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (originUnitTarget != enumUnit && originUnitTarget.canReach(enumUnit, rangeVal)
						&& (multiBounce || !hitUnits.contains(enumUnit))) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId), enumUnit);
					if (condition == null || condition.callback(caster, localStore, castId)) {
						foundUnits.add(enumUnit);
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId));

		CUnit jumpUnit = null;
		if (foundUnits.size() > 0) {
			if (sort != null) {
				ABUnitComparator comp = new ABUnitComparator(caster, localStore, castId, sort);
				foundUnits.sort(comp);
			} else {
				foundUnits.sort(ABNearestUnitComparator.INSTANCE);
			}

			jumpUnit = foundUnits.get(0);
		}

		if (jumpUnit != null) {
			if (lightningId != null) {
				int lidx = 0;
				if (lightningIndex != null) {
					lidx = lightningIndex.callback(caster, localStore, castId);
				}
				localStore.game.createAbilityLightning(originUnitTarget,
						lightningId.callback(caster, localStore, castId), lidx, jumpUnit, 2f);
			}

			if (!multiBounce) {
				hitUnits.add(jumpUnit);
			}
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CHAINUNIT, castId), jumpUnit);
			for (ABAction iterationAction : actions) {
				iterationAction.runAction(caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CHAINUNIT, castId));
		}

		if (remainingJumps > 0) {
			startPerformJump(caster, localStore, castId, jumpUnit, multiBounce, hitUnits, remainingJumps);
		}
	}

}
