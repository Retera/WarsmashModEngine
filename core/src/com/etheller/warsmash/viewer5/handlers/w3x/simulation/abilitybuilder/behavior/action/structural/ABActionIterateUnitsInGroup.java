package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitgroupcallbacks.ABUnitGroupCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionIterateUnitsInGroup implements ABAction {

	private ABUnitGroupCallback unitGroup;
	private List<ABAction> iterationActions;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		Set<CUnit> unitSet = unitGroup.callback(game, caster, localStore, castId);
		List<CUnit> unitList = new ArrayList<>(unitSet); 
		for(CUnit enumUnit : unitList) {
			localStore.put(ABLocalStoreKeys.ENUMUNIT+castId, enumUnit);
			for (ABAction iterationAction : iterationActions) {
				iterationAction.runAction(game, caster, localStore, castId);
			}
		}
		localStore.remove(ABLocalStoreKeys.ENUMUNIT+castId);
	}
}
