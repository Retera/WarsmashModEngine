package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionAddAbility implements ABAction {

	private ABUnitCallback targetUnit;
	private ABAbilityCallback abilityToAdd;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		CAbility ability = abilityToAdd.callback(game, caster, localStore);
		targetUnit.callback(game, caster, localStore).add(game, ability);
		localStore.put(ABLocalStoreKeys.LASTADDEDABILITY, ability);
	}
}
