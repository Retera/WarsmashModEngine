package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionRemoveDefenseBonus implements ABAction {

	private ABUnitCallback targetUnit;
	private ABFloatCallback defenseValue;
	private ABBooleanCallback percentage;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		CUnit target = targetUnit.callback(game, caster, localStore);

		if (percentage.callback(game, caster, localStore)) {
			//TODO need to fix percents
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() - defenseValue.callback(game, caster, localStore));
		} else {
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() - defenseValue.callback(game, caster, localStore));
		}
	}
}
