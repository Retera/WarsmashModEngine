package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.transformation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABDelayInstantTransformationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionTransformUnitInstant implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback keepRatios;

	private ABBooleanCallback permanent; // remove ability after transform
	private ABBooleanCallback requiresPayment;

	private ABFloatCallback transformTime; // the time the unit is locked for the transformation

	private ABBooleanCallback onlyTransformToAlternate;

	private ABFloatCallback duration; // the time before the unit is forced to change back (doesn't charge for it)
	private ABIDCallback buffId;

	private List<ABAction> onTransformActions;
	private List<ABAction> onUntransformActions;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit u1 = caster;
		if (unit != null) {
			u1 = unit.callback(caster, localStore, castId);
		}
		War3ID baseId = null;
		War3ID altId = alternateUnitId.callback(caster, localStore, castId);
		CPlayer pl = localStore.game.getPlayer(u1.getPlayerIndex());
		boolean charge = false;
		boolean addAlternateTagAfter = false;
		boolean perm = false;
		if (permanent != null) {
			perm = permanent.callback(caster, localStore, castId);
		}
		if (this.requiresPayment != null) {
			charge = this.requiresPayment.callback(caster, localStore, castId);
		}
		ABAbilityBuilderAbility abil = localStore.originAbility;

		CUnitType baseType = null;
		if (baseUnitId == null) {
			baseId = u1.getUnitType().getTypeId();
			baseType = u1.getUnitType();
		} else {
			baseId = baseUnitId.callback(caster, localStore, castId);
			baseType = localStore.game.getUnitData().getUnitType(baseId);

		}

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
			targetType = baseType;
			if (perm || targetType.equals(u1.getUnitType())) {
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
		OnTransformationActions unActions = new OnTransformationActions(-goldCost, -lumberCost, null, null,
				onUntransformActions, castId);

		boolean isKeepRatios = true;
		float dur = 0;
		float transTime = 0;
		War3ID theBuffId = null;
		if (keepRatios != null) {
			isKeepRatios = keepRatios.callback(caster, localStore, castId);
		}
		if (permanent != null) {
			perm = permanent.callback(caster, localStore, castId);
		}
		if (duration != null) {
			dur = duration.callback(caster, localStore, castId);
		}
		if (transformTime != null) {
			transTime = transformTime.callback(caster, localStore, castId);
		}
		if (buffId != null) {
			theBuffId = buffId.callback(caster, localStore, castId);
		}

		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.TRANSFORMINGTOALT, castId), addAlternateTagAfter);
		if (transTime > 0) {
			ABTransformationHandler.playMorphAnimation(u1, addAlternateTagAfter);
			new ABDelayInstantTransformationTimer(caster, localStore, u1, actions, addAlternateTagAfter, transTime,
					baseType, targetType, isKeepRatios, abil, theBuffId, transTime, dur).start(localStore.game);
		} else {
			ABTransformationHandler.instantTransformation(localStore, u1, targetType, isKeepRatios, actions, abil,
					addAlternateTagAfter, perm, true);
			if (dur > 0) {
				ABTransformationHandler.createInstantTransformBackBuff(caster, localStore, u1, baseType, isKeepRatios,
						unActions, abil, theBuffId, addAlternateTagAfter, transTime, dur, perm);
			}
		}

	}

}
