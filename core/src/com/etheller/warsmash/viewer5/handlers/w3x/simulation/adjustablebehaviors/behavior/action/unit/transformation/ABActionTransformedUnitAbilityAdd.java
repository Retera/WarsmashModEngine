package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.transformation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;

public class ABActionTransformedUnitAbilityAdd implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback keepRatios;

	private ABBooleanCallback instantTransformAtDurationEnd; // should the transform back be instant?

	private ABBooleanCallback permanent; // remove ability after transform
	private ABBooleanCallback requiresPayment;
	private ABFloatCallback duration; // the time before the unit is forced to change back (doesn't charge for it)
	private ABFloatCallback transformTime; // the time the unit is locked for the transformation (for slow) or spends
											// animating (for instant)
	private ABIDCallback buffId;

	// Used for slow transform back
	private ABBooleanCallback immediateLanding; // true: play morph immediately, false: play morph after
	private ABBooleanCallback immediateTakeOff; // true: play morph immediately, false: play morph after
	private ABFloatCallback altitudeAdjustmentDelay; // time before the unit starts changing height, only applies if
														// going not flying->flying
	private ABFloatCallback altitudeAdjustmentTime; // the time spent changing height, only applies if one type is
													// flying
	private ABFloatCallback landingDelayTime; // Added to the transform time, only applies when going flying->not flying

	private List<ABAction> onTransformActions;
	private List<ABAction> onUntransformActions;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit u1 = caster;
		if (unit != null) {
			u1 = unit.callback(caster, localStore, castId);
		}
		War3ID baseId = baseUnitId.callback(caster, localStore, castId);
		War3ID altId = alternateUnitId.callback(caster, localStore, castId);
		ABAbilityBuilderAbility abil = localStore.originAbility;

		if (baseId == null || altId == null) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId), true);
			return;
		}

		CUnitType baseType = localStore.game.getUnitData().getUnitType(baseId);

		// Always Transforming back
		CUnitType targetType = baseType;
		if (targetType.equals(u1.getUnitType())) {
			// No need to do anything
			return;
		}

		boolean instant = false;
		boolean perm = false;
		boolean charge = false;
		boolean isKeepRatios = true;
		float dur = 0;
		float transTime = 0;
		float landTime = 0;
		float atlAdDelay = 0;
		float altAdTime = 0;
		boolean imLand = false;
		boolean imTakeOff = false;
		War3ID theBuffId = null;
		if (instantTransformAtDurationEnd != null) {
			instant = instantTransformAtDurationEnd.callback(caster, localStore, castId);
		}
		if (permanent != null) {
			perm = permanent.callback(caster, localStore, castId);
		}
		if (this.requiresPayment != null) {
			charge = this.requiresPayment.callback(caster, localStore, castId);
		}
		if (keepRatios != null) {
			isKeepRatios = keepRatios.callback(caster, localStore, castId);
		}
		if (duration != null) {
			dur = duration.callback(caster, localStore, castId);
		}
		if (transformTime != null) {
			transTime = transformTime.callback(caster, localStore, castId);
		}
		if (landingDelayTime != null) {
			landTime = landingDelayTime.callback(caster, localStore, castId);
		}
		if (altitudeAdjustmentDelay != null) {
			atlAdDelay = altitudeAdjustmentDelay.callback(caster, localStore, castId);
		}
		if (altitudeAdjustmentTime != null) {
			altAdTime = altitudeAdjustmentTime.callback(caster, localStore, castId);
		}
		if (immediateLanding != null) {
			imLand = immediateLanding.callback(caster, localStore, castId);
		}
		if (immediateTakeOff != null) {
			imTakeOff = immediateTakeOff.callback(caster, localStore, castId);
		}
		if (buffId != null) {
			theBuffId = buffId.callback(caster, localStore, castId);
		}

		ABTransformationHandler.setTags(u1, true);
		if (perm) {
			u1.remove(localStore.game, abil);
		}
		if (onTransformActions != null) {
			for (ABAction action : onTransformActions) {
				action.runAction(u1, localStore, castId);
			}
		}

		if (dur > 0 && !perm) {
			OnTransformationActions actions;
			if (charge) {
				int goldCost = 0;
				int lumberCost = 0;
				Integer foodCost = null;
				if (localStore.game.getGameplayConstants().isRelativeUpgradeCosts()) {

					goldCost = baseType.getGoldCost() - u1.getUnitType().getGoldCost();
					lumberCost = baseType.getLumberCost() - u1.getUnitType().getLumberCost();
				} else {
					goldCost = baseType.getGoldCost();
					lumberCost = baseType.getLumberCost();
				}
				actions = new OnTransformationActions(goldCost, lumberCost, foodCost, null, onUntransformActions,
						castId);
			} else {
				actions = new OnTransformationActions(onUntransformActions, castId);
			}

			if (instant) {
				ABTransformationHandler.createInstantTransformBackBuff(caster, localStore, u1, baseType, isKeepRatios,
						actions, abil, theBuffId, true, transTime, dur, perm);
			} else {
				boolean takingOff = u1.getMovementType() != MovementType.FLY
						&& baseType.getMovementType() == MovementType.FLY;
				boolean landing = u1.getMovementType() == MovementType.FLY
						&& baseType.getMovementType() != MovementType.FLY;
				ABTransformationHandler.createSlowTransformBackBuff(caster, localStore, u1, baseType, isKeepRatios,
						actions, abil, theBuffId, true, transTime, dur, perm, takingOff, landing, imTakeOff, imLand,
						atlAdDelay, landTime, altAdTime);
			}
		}

	}

}
