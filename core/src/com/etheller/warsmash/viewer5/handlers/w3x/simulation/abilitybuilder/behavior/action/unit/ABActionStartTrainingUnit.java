package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionStartTrainingUnit implements ABSingleAction {

	private ABUnitCallback unit;
	private ABIDCallback id;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CUnit theUnit = this.unit.callback(game, caster, localStore, castId);
		theUnit.queueTrainingUnit(game, this.id.callback(game, caster, localStore, castId));
		theUnit.notifyOrdersChanged();
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "StartTrainingUnit(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.id.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
