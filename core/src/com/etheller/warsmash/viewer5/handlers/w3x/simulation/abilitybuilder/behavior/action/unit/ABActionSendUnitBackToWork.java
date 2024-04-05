package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.ABTimer;

public class ABActionSendUnitBackToWork implements ABAction {

	private ABUnitCallback unit;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CUnit targetUnit;
		if (unit != null) {
			targetUnit = unit.callback(game, caster, localStore, castId);
		} else {
			targetUnit = caster;
		}
		
		ABTimer timer = new ABTimer(caster, localStore, null, castId) {
			@Override 
			public void onFire(CSimulation simulation) {
				targetUnit.backToWork(game, null);
			}
		};
		timer.setRepeats(false);
		timer.setTimeoutTime(0f);
		timer.start(game);
	}

}
