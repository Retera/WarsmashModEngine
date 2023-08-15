
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks.ABNonStackingStatBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABActionUpdateNonStackingStatBuff implements ABAction {

	private ABNonStackingStatBuffCallback buff;
	private ABFloatCallback value;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		NonStackingStatBuff buffObj = buff.callback(game, caster, localStore, castId);
		System.err.println("Updating Stat buff to: " + value.callback(game, caster, localStore, castId));
		buffObj.setValue(value.callback(game, caster, localStore, castId));
	}
}