package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.ABTimer;

public class ABActionSendUnitBackToWork implements ABSingleAction {

	private ABUnitCallback unit;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CUnit targetUnit;
		if (this.unit != null) {
			targetUnit = this.unit.callback(game, caster, localStore, castId);
		}
		else {
			targetUnit = caster;
		}

		final ABTimer timer = new ABTimer(caster, localStore, null, castId) {
			@Override
			public void onFire(CSimulation simulation) {
				targetUnit.backToWork(game, null);
			}
		};
		timer.setRepeats(false);
		timer.setTimeoutTime(0f);
		timer.start(game);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String unitExpression;
		if (this.unit != null) {
			unitExpression = this.unit.generateJassEquivalent(jassTextGenerator);
		}
		else {
			unitExpression = jassTextGenerator.getCaster();
		}
		return "SendUnitBackToWorkAU(" + unitExpression + ", " + jassTextGenerator.getTriggerLocalStore() + ", "
				+ jassTextGenerator.getCastId() + ")";
	}
}
