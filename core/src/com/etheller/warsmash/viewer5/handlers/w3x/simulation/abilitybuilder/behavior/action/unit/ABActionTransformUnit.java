package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorFinishTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;

public class ABActionTransformUnit implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback immediateLanding; // true: play morph immediately, false: play morph after
	private ABBooleanCallback immediateTakeOff; // true: play morph immediately, false: play morph after
	private ABBooleanCallback permanent; // remove ability after transform
	private ABBooleanCallback requiresPayment;
//	private ABBooleanCallback uninterruptable;

	private ABFloatCallback altitudeAdjustmentDelay; // time before the unit starts changing height, only applies if
														// going not flying->flying
	private ABFloatCallback altitudeAdjustmentTime; // the time spent changing height, only applies if one type is
													// flying
	private ABFloatCallback landingDelayTime; // Added to the transform time, only applies when going flying->not flying

	private ABFloatCallback transformTime; // the time the unit is locked for the transformation

	private ABFloatCallback duration; // the time before the unit is forced to change back (doesn't charge for it)
	private ABIDCallback buffId;
	private ABBooleanCallback instantTransformAtDurationEnd;

	private List<ABAction> onTransformActions;
	private List<ABAction> onUntransformActions;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit u1 = caster;
		if (unit != null) {
			u1 = unit.callback(game, caster, localStore, castId);
		}
		War3ID baseId = baseUnitId.callback(game, caster, localStore, castId);
		War3ID altId = alternateUnitId.callback(game, caster, localStore, castId);
		CPlayer pl = game.getPlayer(u1.getPlayerIndex());
		boolean charge = false;
		boolean addAlternateTagAfter = false;
		if (this.requiresPayment != null) {
			charge = this.requiresPayment.callback(game, caster, localStore, castId);
		}
		AbilityBuilderActiveAbility abil = (AbilityBuilderActiveAbility) localStore.get(ABLocalStoreKeys.ABILITY);

		if (baseId == null || altId == null) {
			localStore.put(ABLocalStoreKeys.FAILEDTOCAST + castId, true);
			return;
		}

		CUnitType targetType = null;

		if (u1.getTypeId().equals(altId)) {
			// Transforming back
			targetType = game.getUnitData().getUnitType(baseId);
			if (targetType.equals(u1.getUnitType())) {
				// No need to do anything
				return;
			}
		} else {
			// Transforming to alt
			addAlternateTagAfter = true;
			targetType = game.getUnitData().getUnitType(altId);
		}

		int goldCost = 0;
		int lumberCost = 0;
		Integer foodCost = null;
		if (charge) {
			if (game.getGameplayConstants().isRelativeUpgradeCosts()) {
				goldCost = targetType.getGoldCost() - u1.getUnitType().getGoldCost();
				lumberCost = targetType.getLumberCost() - u1.getUnitType().getLumberCost();
				if (goldCost > pl.getGold() || lumberCost > pl.getLumber()) {
					localStore.put(ABLocalStoreKeys.FAILEDTOCAST + castId, true);
					return;
				}
			} else {
				goldCost = targetType.getGoldCost();
				lumberCost = targetType.getLumberCost();
				if (goldCost > pl.getGold() || lumberCost > pl.getLumber()) {
					localStore.put(ABLocalStoreKeys.FAILEDTOCAST + castId, true);
					return;
				}
			}
			foodCost = Math.max(targetType.getFoodUsed() - u1.getUnitType().getFoodUsed(), 0);
			if (foodCost > 0 && pl.getFoodUsed() + foodCost > pl.getFoodCap()) {
				localStore.put(ABLocalStoreKeys.FAILEDTOCAST + castId, true);
				return;
			}
		}
		OnTransformationActions actions = new OnTransformationActions(goldCost, lumberCost, foodCost, onTransformActions, onUntransformActions);

		boolean perm = false;
		float dur = 0;
		float transTime = 0;
		float landTime = 0;
		float atlAdDelay = 0;
		float altAdTime = 0;
		boolean imLand = false;
		boolean imTakeOff = false;
		War3ID theBuffId = null;
		boolean instant = false;
		if (permanent != null) {
			perm = permanent.callback(game, caster, localStore, castId);
		}
		if (duration != null) {
			dur = duration.callback(game, caster, localStore, castId);
		}
		if (transformTime != null) {
			transTime = transformTime.callback(game, caster, localStore, castId);
		}
		if (landingDelayTime != null) {
			landTime = landingDelayTime.callback(game, caster, localStore, castId);
		}
		if (altitudeAdjustmentDelay != null) {
			atlAdDelay = altitudeAdjustmentDelay.callback(game, caster, localStore, castId);
		}
		if (altitudeAdjustmentTime != null) {
			altAdTime = altitudeAdjustmentTime.callback(game, caster, localStore, castId);
		}
		if (immediateLanding != null) {
			imLand = immediateLanding.callback(game, caster, localStore, castId);
		}
		if (immediateTakeOff != null) {
			imTakeOff = immediateTakeOff.callback(game, caster, localStore, castId);
		}
		if (buffId != null) {
			theBuffId = buffId.callback(game, caster, localStore, castId);
		}
		if (instantTransformAtDurationEnd != null) {
			instant = instantTransformAtDurationEnd.callback(game, caster, localStore, castId);
		}

		localStore.put(ABLocalStoreKeys.TRANSFORMINGTOALT + castId, addAlternateTagAfter);
		localStore.put(ABLocalStoreKeys.NEWBEHAVIOR,
				new CBehaviorFinishTransformation(localStore, u1, abil, targetType, actions, addAlternateTagAfter,
						addAlternateTagAfter ? abil.getBaseOrderId() : abil.getOffOrderId(), perm, dur, transTime,
						landTime, atlAdDelay, altAdTime, imLand, imTakeOff, theBuffId,
						game.getUnitData().getUnitType(baseId), instant));

	}

}
