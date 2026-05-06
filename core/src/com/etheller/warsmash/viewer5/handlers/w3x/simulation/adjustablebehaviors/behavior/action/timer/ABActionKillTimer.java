package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.timer;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.timer.ABTimerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionKillTimer implements ABSingleAction {

	private ABTimerCallback timer;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		this.timer.callback(caster, localStore, castId).kill(localStore.game);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "PauseTimer(" + this.timer.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
