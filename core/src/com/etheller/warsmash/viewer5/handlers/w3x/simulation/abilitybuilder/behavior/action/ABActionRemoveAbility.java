package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionRemoveAbility implements ABAction {

	private ABUnitCallback targetUnit;
	private ABAbilityCallback abilityToRemove;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		(targetUnit.callback(game, caster, localStore)).remove(game,
				abilityToRemove.callback(game, caster, localStore));
	}
}
