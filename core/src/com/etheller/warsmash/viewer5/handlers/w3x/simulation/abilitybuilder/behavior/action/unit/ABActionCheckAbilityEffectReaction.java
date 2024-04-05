package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionCheckAbilityEffectReaction implements ABAction {

	private ABUnitCallback target;
	private List<ABAction> onHitActions;
	private List<ABAction> onBlockActions;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit tarU = caster;
		if (target != null) {
			tarU = target.callback(game, caster, localStore, castId);
		}
		if (tarU.checkForAbilityEffectReaction(game, caster, (CAbility) localStore.get(ABLocalStoreKeys.ABILITY))) {
			if (onHitActions != null) {
				for (ABAction periodicAction : onHitActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		} else {
			if (onBlockActions != null) {
				for (ABAction periodicAction : onBlockActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}
	}
}
