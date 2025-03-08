package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public class CTimerSleepAction extends CTimer {
	private final JassThread sleepingThread;

	public CTimerSleepAction(final JassThread sleepingThread) {
		this.sleepingThread = sleepingThread;
	}

	@Override
	public void onFire(final CSimulation simulation) {
		this.sleepingThread.setSleeping(false);
	}

}
