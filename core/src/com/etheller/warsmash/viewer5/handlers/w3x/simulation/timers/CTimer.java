package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public abstract class CTimer {
	private int engineFireTick;
	private int scheduleTick;
	private float timeoutTime;
	private float remainingTimeAfterPause;
	private boolean running = false;
	private boolean repeats;

	public void setTimeoutTime(final float timeoutTime) {
		this.timeoutTime = timeoutTime;
	}

	public boolean isRepeats() {
		return this.repeats;
	}

	public boolean isRunning() {
		return this.running;
	}

	public float getTimeoutTime() {
		return this.timeoutTime;
	}

	/**
	 * @param simulation
	 */
	public void start(final CSimulation simulation) {
		this.running = true;
		final int currentTick = simulation.getGameTurnTick();
		this.scheduleTick = currentTick;
		innerStart(this.timeoutTime, simulation, currentTick);
	}

	private void innerStart(final float timeoutTime, final CSimulation simulation, final int currentTick) {
		final int ticks = (int) (timeoutTime / WarsmashConstants.SIMULATION_STEP_TIME);
		this.engineFireTick = currentTick + ticks;
		simulation.registerTimer(this);
	}

	public void pause(final CSimulation simulation) {
		this.remainingTimeAfterPause = getRemaining(simulation);
		simulation.unregisterTimer(this);
	}

	public void resume(final CSimulation simulation) {
		if (this.remainingTimeAfterPause == 0) {
			start(simulation);
		}
		final int currentTick = simulation.getGameTurnTick();
		innerStart(this.remainingTimeAfterPause, simulation, currentTick);
		this.remainingTimeAfterPause = 0;
	}

	public float getElapsed(final CSimulation simulation) {
		final int currentTick = simulation.getGameTurnTick();
		final int elapsedTicks = currentTick - this.scheduleTick;
		return Math.max(elapsedTicks * WarsmashConstants.SIMULATION_STEP_TIME, this.timeoutTime);

	}

	public float getRemaining(final CSimulation simulation) {
		return this.timeoutTime - getElapsed(simulation);
	}

	public void setRepeats(final boolean repeats) {
		this.repeats = repeats;
	}

	public int getEngineFireTick() {
		return this.engineFireTick;
	}

	public abstract void onFire();

	public void fire(final CSimulation simulation) {
		// its implied that we will have "unregisterTimer" happen automatically
		// before this is called
		this.running = false;
		onFire();
		if (this.repeats) {
			start(simulation);
		}
	}
}
