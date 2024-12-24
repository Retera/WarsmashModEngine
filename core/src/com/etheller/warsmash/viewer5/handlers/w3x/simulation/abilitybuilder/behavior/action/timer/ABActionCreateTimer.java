package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.timer;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.ABTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionCreateTimer implements ABSingleAction {

	private ABFloatCallback timeout;
	private ABBooleanCallback repeats;
	private List<ABAction> actions;
	private ABBooleanCallback startTimer;
	private ABFloatCallback delay;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {

		final CTimer timer = new ABTimer(caster, localStore, this.actions, castId);
		timer.setTimeoutTime(this.timeout.callback(game, caster, localStore, castId));
		localStore.put(ABLocalStoreKeys.LASTCREATEDTIMER, timer);

		if ((this.repeats != null) && this.repeats.callback(game, caster, localStore, castId)) {
			timer.setRepeats(true);
			if ((this.startTimer == null) || this.startTimer.callback(game, caster, localStore, castId)) {
				if (this.delay != null) {
					timer.startRepeatingTimerWithDelay(game, this.delay.callback(game, caster, localStore, castId));
				}
				else {
					timer.start(game);
				}
				localStore.put(ABLocalStoreKeys.LASTSTARTEDTIMER, timer);
			}
		}
		else {
			if ((this.startTimer == null) || this.startTimer.callback(game, caster, localStore, castId)) {
				timer.start(game);
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
		}
		else {
			return "CreateTimerAU(" + args + ")";
		}
	}
}
