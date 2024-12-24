package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.handler.TransformationHandler;

public class ABActionTransformedUnitAbilityRemove implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	private ABBooleanCallback permanent;

	private List<ABAction> onUntransformActions;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		boolean perm = false;
		if (permanent != null) {
			perm = permanent.callback(game, caster, localStore, castId);
		}
		if (!perm) {
			CUnit u1 = caster;
			if (unit != null) {
				u1 = unit.callback(game, caster, localStore, castId);
			}
			War3ID baseId = baseUnitId.callback(game, caster, localStore, castId);
			War3ID altId = alternateUnitId.callback(game, caster, localStore, castId);
			AbilityBuilderActiveAbility abil = (AbilityBuilderActiveAbility) localStore.get(ABLocalStoreKeys.ABILITY);

			if (baseId == null || altId == null) {
				return;
			}

			// Only care if already transformed
			CUnitType targetType = game.getUnitData().getUnitType(altId);
			if (!targetType.equals(u1.getUnitType())) {
				// No need to do anything
				return;
			}


			CUnitType baseType = game.getUnitData().getUnitType(baseId);

			if (onUntransformActions != null) {
				for (ABAction action : onUntransformActions) {
					action.runAction(game, u1, localStore, castId);
				}
			}
			TransformationHandler.setUnitID(game, localStore, u1, baseType, perm, null, abil, true);
		}
	}

}
