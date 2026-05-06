package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.worker;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABTimer;

public class ABActionSendUnitBackToWork implements ABSingleAction {

	private ABUnitCallback unit;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final CUnit targetUnit;
		if (this.unit != null) {
			targetUnit = this.unit.callback(caster, localStore, castId);
		} else {
			targetUnit = caster;
		}

		final ABTimer timer = new ABTimer(caster, localStore, null, castId) {
			@Override
			public void onFire(CSimulation simulation) {
				targetUnit.backToWork(localStore.game, null);
			}
		};
		timer.setRepeats(false);
		timer.setTimeoutTime(0f);
		timer.start(localStore.game);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String unitExpression;
		if (this.unit != null) {
			unitExpression = this.unit.generateJassEquivalent(jassTextGenerator);
		} else {
			unitExpression = jassTextGenerator.getCaster();
		}
		return "SendUnitBackToWorkAU(" + unitExpression + ", " + jassTextGenerator.getTriggerLocalStore() + ", "
				+ jassTextGenerator.getCastId() + ")";
	}
}
