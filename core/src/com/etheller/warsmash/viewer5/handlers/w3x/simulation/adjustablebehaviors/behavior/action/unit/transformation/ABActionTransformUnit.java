package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.transformation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorFinishTransformation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABTransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABActionTransformUnit implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback keepRatios;

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

	private ABBooleanCallback onlyTransformToAlternate;

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
		CPlayer pl = localStore.game.getPlayer(u1.getPlayerIndex());
		boolean charge = false;
		boolean addAlternateTagAfter = false;
		if (this.requiresPayment != null) {
			charge = this.requiresPayment.callback(caster, localStore, castId);
		}
		ABAbilityBuilderAbility abil = localStore.originAbility;

		if (baseId == null || altId == null) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId), true);
			return;
		}

		CUnitType targetType = null;

		boolean onlyToAlt = false;
		if (onlyTransformToAlternate != null) {
			onlyToAlt = onlyTransformToAlternate.callback(caster, localStore, castId);
		}

		if (!onlyToAlt && u1.getTypeId().equals(altId)) {
			// Transforming back
			targetType = localStore.game.getUnitData().getUnitType(baseId);
			if (targetType.equals(u1.getUnitType())) {
				// No need to do anything
				return;
			}
		} else {
			// Transforming to alt
			addAlternateTagAfter = true;
			targetType = localStore.game.getUnitData().getUnitType(altId);
		}

		int goldCost = 0;
		int lumberCost = 0;
		Integer foodCost = null;
		if (charge) {
			if (localStore.game.getGameplayConstants().isRelativeUpgradeCosts()) {
				goldCost = targetType.getGoldCost() - u1.getUnitType().getGoldCost();
				lumberCost = targetType.getLumberCost() - u1.getUnitType().getLumberCost();
				if (goldCost > pl.getGold() || lumberCost > pl.getLumber()) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId), true);
					return;
				}
			} else {
				goldCost = targetType.getGoldCost();
				lumberCost = targetType.getLumberCost();
				if (goldCost > pl.getGold() || lumberCost > pl.getLumber()) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId), true);
					return;
				}
			}
			foodCost = Math.max(targetType.getFoodUsed() - u1.getUnitType().getFoodUsed(), 0);
			if (foodCost > 0 && pl.getFoodUsed() + foodCost > pl.getFoodCap()) {
				localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId), true);
				return;
			}
		}
		OnTransformationActions actions = new OnTransformationActions(goldCost, lumberCost, foodCost,
				onTransformActions, onUntransformActions, castId);

		boolean perm = false;
		boolean isKeepRatios = true;
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
			perm = permanent.callback(caster, localStore, castId);
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
		if (instantTransformAtDurationEnd != null) {
			instant = instantTransformAtDurationEnd.callback(caster, localStore, castId);
		}

		if (transTime > 0) {
			int orderId = -1;
			if (abil instanceof ABAbilityBuilderActiveAbility) {
				ABAbilityBuilderActiveAbility activeabil = (ABAbilityBuilderActiveAbility) abil;
				orderId = addAlternateTagAfter ? activeabil.getBaseOrderId() : activeabil.getOffOrderId();
			}

			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.TRANSFORMINGTOALT, castId),
					addAlternateTagAfter);
			localStore.put(ABLocalStoreKeys.NEWBEHAVIOR,
					new ABBehaviorFinishTransformation(caster, localStore, u1, abil, targetType, isKeepRatios, actions,
							addAlternateTagAfter, orderId, perm, dur, transTime, landTime, atlAdDelay, altAdTime,
							imLand, imTakeOff, theBuffId, localStore.game.getUnitData().getUnitType(baseId), instant));
		} else {
			ABTransformationHandler.instantTransformation(localStore, u1, targetType, isKeepRatios, actions, abil,
					addAlternateTagAfter, perm, true);
			if (dur > 0) {
				OnTransformationActions unActions = new OnTransformationActions(-goldCost, -lumberCost, null, null,
						onUntransformActions, castId);
				ABTransformationHandler.createInstantTransformBackBuff(caster, localStore, u1,
						localStore.game.getUnitData().getUnitType(baseId), isKeepRatios, unActions, abil, theBuffId,
						addAlternateTagAfter, transTime, dur, perm);
			}
			u1.fireSpellEvents(localStore.game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, abil, null);
			u1.fireSpellEvents(localStore.game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, abil, null);
		}

	}

}
