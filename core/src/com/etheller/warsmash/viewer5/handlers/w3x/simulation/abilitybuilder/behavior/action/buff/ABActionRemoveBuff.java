package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.buff;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionRemoveBuff implements ABAction {

	private ABUnitCallback target;
	private ABBuffCallback buff;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		(target.callback(game, caster, localStore)).remove(game, buff.callback(game, caster, localStore));
	}
}
