package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionSetExplodesOnDeath implements ABAction {

	private ABUnitCallback unit;
	private ABBooleanCallback explodes;
	private ABIDCallback buffId;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit targetUnit = unit.callback(game, caster, localStore, castId);
		targetUnit.setExplodesOnDeath(explodes.callback(game, caster, localStore, castId));
		if (buffId != null) {
			targetUnit.setExplodesOnDeathBuffId(buffId.callback(game, caster, localStore, castId));
		}
	}

}
