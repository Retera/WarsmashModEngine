package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.timer;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionCreateTimer implements ABSingleAction {

	private ABFloatCallback timeout;
	private ABBooleanCallback repeats;
	private List<ABAction> actions;
	private ABBooleanCallback startTimer;
	private ABFloatCallback delay;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {

		final CTimer timer = new ABTimer(caster, localStore, this.actions, castId);
		timer.setTimeoutTime(this.timeout.callback(caster, localStore, castId));
		localStore.put(ABLocalStoreKeys.LASTCREATEDTIMER, timer);

		if ((this.repeats != null) && this.repeats.callback(caster, localStore, castId)) {
			timer.setRepeats(true);
			if ((this.startTimer == null) || this.startTimer.callback(caster, localStore, castId)) {
				if (this.delay != null) {
					timer.startRepeatingTimerWithDelay(localStore.game, this.delay.callback(caster, localStore, castId));
				} else {
					timer.start(localStore.game);
				}
				localStore.put(ABLocalStoreKeys.LASTSTARTEDTIMER, timer);
			}
		} else {
			if ((this.startTimer == null) || this.startTimer.callback(caster, localStore, castId)) {
				timer.start(localStore.game);
				localStore.put(ABLocalStoreKeys.LASTSTARTEDTIMER, timer);
			}
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		final String funcName = jassTextGenerator.createAnonymousFunction(this.actions, "CreateTimerAU_OnTimerFire");

		String repeatsExpression = "false";
		if (this.repeats != null) {
			repeatsExpression = this.repeats.generateJassEquivalent(jassTextGenerator);
		}

		String startTimerExpression = "true";
		if (this.startTimer != null) {
			startTimerExpression = this.startTimer.generateJassEquivalent(jassTextGenerator);
		}

		final String args = jassTextGenerator.getCaster() + ", " + jassTextGenerator.getTriggerLocalStore() + ", "
				+ jassTextGenerator.getCastId() + ", " + this.timeout.generateJassEquivalent(jassTextGenerator) + ", "
				+ repeatsExpression + ", " + jassTextGenerator.functionPointerByName(funcName) + ", "
				+ startTimerExpression;

		if (this.delay != null) {
			return "CreateTimerDelayedAU(" + args + ", " + this.delay.generateJassEquivalent(jassTextGenerator) + ")";
		} else {
			return "CreateTimerAU(" + args + ")";
		}
	}
}
