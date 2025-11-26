package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.iterstructs.ABNearestUnitComparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.iterstructs.ABUnitComparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionChainEffect implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();
	private List<ABAction> actions;
	private ABCondition condition;
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
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CUnit originUnitTarget = this.target.callback(game, caster, localStore, castId);
		boolean multiBounce = game.getGameplayConstants().isAllowMultiBounce();
		if (allowMultipleBouncesPerUnit != null) {
			multiBounce = allowMultipleBouncesPerUnit.callback(game, caster, localStore, castId);
		}
		Set<CUnit> hitUnits = null;
		if (!multiBounce) {
			hitUnits = new HashSet<>();
			hitUnits.add(originUnitTarget);
		}

		if (lightningFromCaster == null || lightningFromCaster.callback(game, caster, localStore, castId)) {
			if (lightningId != null) {
				int lidx = 0;
				if (lightningIndex != null) {
					lidx = lightningIndex.callback(game, caster, localStore, castId);
				}
				game.createAbilityLightning(caster, lightningId.callback(game, caster, localStore, castId), lidx,
						originUnitTarget, 2f);
			}
		}

		localStore.put(ABLocalStoreKeys.CHAINUNIT + castId, originUnitTarget);
		for (ABAction iterationAction : actions) {
			iterationAction.runAction(game, caster, localStore, castId);
		}
		localStore.remove(ABLocalStoreKeys.CHAINUNIT + castId);

		startPerformJump(game, caster, localStore, castId, originUnitTarget, multiBounce, hitUnits,
				bounces.callback(game, caster, localStore, castId));

	}

	private void startPerformJump(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId, final CUnit originUnitTarget, final boolean multiBounce, final Set<CUnit> hitUnits,
			final int remainingJumps) {
		if (remainingJumps <= 0) {
			return;
		}
		float delay = 0;
		if (bounceDelay != null) {
			delay = bounceDelay.callback(game, caster, localStore, castId);
		}
		if (delay > 0) {
			CTimer runner = new CTimer() {
				@Override
				public void onFire(CSimulation simulation) {
					performJump(game, caster, localStore, castId, originUnitTarget, multiBounce, hitUnits,
							remainingJumps - 1);
				}
			};
			runner.setTimeoutTime(delay);
			runner.start(game);
		} else {
			performJump(game, caster, localStore, castId, originUnitTarget, multiBounce, hitUnits, remainingJumps - 1);
		}
	}

	private void performJump(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId, final CUnit originUnitTarget, final boolean multiBounce, final Set<CUnit> hitUnits,
			final int remainingJumps) {
		if (originUnitTarget == null) {
			return;
		}

		final Float rangeVal = this.range.callback(game, caster, localStore, castId);

		List<CUnit> foundUnits = new ArrayList<>();
		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (originUnitTarget != enumUnit && originUnitTarget.canReach(enumUnit, rangeVal)
						&& (multiBounce || !hitUnits.contains(enumUnit))) {
					localStore.put(ABLocalStoreKeys.MATCHINGUNIT + castId, enumUnit);
					if (condition == null || condition.callback(game, caster, localStore, castId)) {
						foundUnits.add(enumUnit);
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.MATCHINGUNIT + castId);

		CUnit jumpUnit = null;
		if (foundUnits.size() > 0) {
			if (sort != null) {
				ABUnitComparator comp = new ABUnitComparator(game, caster, localStore, castId, sort);
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
					lidx = lightningIndex.callback(game, caster, localStore, castId);
				}
				game.createAbilityLightning(originUnitTarget, lightningId.callback(game, caster, localStore, castId),
						lidx, jumpUnit, 2f);
			}

			if (!multiBounce) {
				hitUnits.add(jumpUnit);
			}
			localStore.put(ABLocalStoreKeys.CHAINUNIT + castId, jumpUnit);
			for (ABAction iterationAction : actions) {
				iterationAction.runAction(game, caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.CHAINUNIT + castId);
		}

		if (remainingJumps > 0) {
			startPerformJump(game, caster, localStore, castId, jumpUnit, multiBounce, hitUnits, remainingJumps);
		}
	}

}
