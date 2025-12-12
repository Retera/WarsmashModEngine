package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionMultiplyUnitScale implements ABSingleAction {

	private ABUnitCallback unit;
	private ABFloatCallback value;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CUnit targetUnit = this.unit.callback(game, caster, localStore, castId);
		game.changeUnitScale(targetUnit, value.callback(game, caster, localStore, castId), true);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}
}
