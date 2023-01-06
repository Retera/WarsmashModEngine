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

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		Set<CUnit> unitSet = unitGroup.callback(game, caster, localStore);
		List<CUnit> unitList = new ArrayList<>(unitSet); 
		for(CUnit enumUnit : unitList) {
			localStore.put(ABLocalStoreKeys.ENUMUNIT, enumUnit);
			for (ABAction iterationAction : iterationActions) {
				iterationAction.runAction(game, caster, localStore);
			}
		}
	}
}
