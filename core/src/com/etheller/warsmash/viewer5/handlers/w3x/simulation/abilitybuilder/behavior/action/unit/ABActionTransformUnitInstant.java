package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler.OnTransformationActions;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.DelayInstantTransformationTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionTransformUnitInstant implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback permanent; // remove ability after transform
	private ABBooleanCallback requiresPayment;

	private ABFloatCallback transformTime; // the time the unit is locked for the transformation

	private ABFloatCallback duration; // the time before the unit is forced to change back (doesn't charge for it)
	private ABIDCallback buffId;

	private List<ABAction> onTransformActions;
	private List<ABAction> onUntransformActions;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit u1 = caster;
		if (unit != null) {
			u1 = unit.callback(game, caster, localStore, castId);
		}
		War3ID baseId = null;
		War3ID altId = alternateUnitId.callback(game, caster, localStore, castId);
		CPlayer pl = game.getPlayer(u1.getPlayerIndex());
		boolean charge = false;
		boolean addAlternateTagAfter = false;
		boolean perm = false;
		if (permanent != null) {
			perm = permanent.callback(game, caster, localStore, castId);
		}
		if (this.requiresPayment != null) {
			charge = this.requiresPayment.callback(game, caster, localStore, castId);
		}
		AbilityBuilderAbility abil = (AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY);

		CUnitType baseType = null;
		if (baseUnitId == null) {
			baseId = u1.getUnitType().getTypeId();
			baseType = u1.getUnitType();
		} else {
			baseId = baseUnitId.callback(game, caster, localStore, castId);
			baseType = game.getUnitData().getUnitType(baseId);

		}

		if (baseId == null || altId == null) {
			localStore.put(ABLocalStoreKeys.FAILEDTOCAST + castId, true);
			return;
		}

		CUnitType targetType = null;

		if (u1.getTypeId().equals(altId)) {
			// Transforming back
			targetType = baseType;
			if (perm || targetType.equals(u1.getUnitType())) {
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
		OnTransformationActions actions = new OnTransformationActions(goldCost, lumberCost, foodCost,
				onTransformActions, onUntransformActions);
		OnTransformationActions unActions = new OnTransformationActions(-goldCost, -lumberCost, null,
				null, onUntransformActions);

		float dur = 0;
		float transTime = 0;
		War3ID theBuffId = null;
		if (permanent != null) {
			perm = permanent.callback(game, caster, localStore, castId);
		}
		if (duration != null) {
			dur = duration.callback(game, caster, localStore, castId);
		}
		if (transformTime != null) {
			transTime = transformTime.callback(game, caster, localStore, castId);
		}
		if (buffId != null) {
			theBuffId = buffId.callback(game, caster, localStore, castId);
		}

		localStore.put(ABLocalStoreKeys.TRANSFORMINGTOALT + castId, addAlternateTagAfter);
		if (transTime > 0) {
			TransformationHandler.playMorphAnimation(u1, addAlternateTagAfter);
			new DelayInstantTransformationTimer(game, localStore, u1, actions, addAlternateTagAfter, transTime,
					baseType, targetType, abil, theBuffId, transTime, dur).start(game);
		} else {
			TransformationHandler.instantTransformation(game, localStore, u1, targetType, actions, abil,
					addAlternateTagAfter, perm, true);
			if (dur > 0) {
				TransformationHandler.createInstantTransformBackBuff(game, localStore, u1, baseType,
						unActions, abil, theBuffId, addAlternateTagAfter,
						transTime, dur, perm);
			}
		}

	}

}
