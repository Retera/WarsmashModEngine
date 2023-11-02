package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class DelayTimerTimer extends CTimer {

	private CTimer timer;

	public DelayTimerTimer(CTimer timer, float delay) {
		super();
		this.timer = timer;
		this.setRepeats(false);
		this.setTimeoutTime(delay);
	}

	public void onFire(CSimulation game) {
		timer.start(game);
	}
	
}
