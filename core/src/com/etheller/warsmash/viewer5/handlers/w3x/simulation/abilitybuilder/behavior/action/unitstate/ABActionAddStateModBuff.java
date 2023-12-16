
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitstate;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statemodcallbacks.ABStateModBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;

public class ABActionAddStateModBuff implements ABAction {

	private ABUnitCallback targetUnit;
	private ABStateModBuffCallback buff;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CUnit target = targetUnit.callback(game, caster, localStore, castId);
		StateModBuff theBuff = buff.callback(game, caster, localStore, castId);
		
		target.addStateModBuff(theBuff);
		target.computeUnitState(game, theBuff.getBuffType());
	}
}