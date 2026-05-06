package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.timer;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.timer.ABTimerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionUpdateTimerTimeout implements ABSingleAction {

	private ABTimerCallback timer;
	private ABFloatCallback timeout;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		CTimer theT = this.timer.callback(caster, localStore, castId);
		theT.resetTimeoutTime(this.timeout.callback(caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "ABTimerSetTimeoutTime(" + this.timer.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.timeout.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
