
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABNonStackingStatBuffTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionRecomputeStatBuffsOnUnit implements ABAction {

	private ABUnitCallback targetUnit;
	private ABNonStackingStatBuffTypeCallback buffType;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		CUnit unit = targetUnit.callback(game, caster, localStore);
		unit.computeDerivedFields(buffType.callback(game, caster, localStore));
	}
}