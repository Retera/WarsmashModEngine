package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionAddDefenseBonus implements ABAction {

	private ABUnitCallback targetUnit;
	private ABFloatCallback defenseValue;
	private ABBooleanCallback percentage;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CUnit target = targetUnit.callback(game, caster, localStore, castId);

		if (percentage.callback(game, caster, localStore, castId)) {
			//TODO need to fix percents
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() + defenseValue.callback(game, caster, localStore, castId));
		} else {
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() + defenseValue.callback(game, caster, localStore, castId));
		}
	}
}
