package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public abstract class ABTerrainEffect extends CTimer implements SimulationRenderComponent {

	private boolean removing = false;

	private float period;
	protected int totalCount;
	
	private float elapsedTime;
	private float startTime;
	protected int count;
	private boolean loop;
	
	protected boolean stopping;
	protected float stopDuration;
	private float stopBegan;

	public ABTerrainEffect(float period, int totalCount) {
		this.period = period;
		this.totalCount = Math.max(totalCount, 1);
		
		this.setRepeats(true);
		this.setTimeoutTime(0);
		this.startTime = 0;
		this.elapsedTime = 0;
		this.count = 0;
		this.loop = period < 0;
		
		this.stopping = false;
		this.stopDuration = 0;
		this.stopBegan = 0;
	}

	@Override
	public void remove() {
		if (this.stopDuration > 0) {
			this.stopBegan = this.elapsedTime;
			this.stopping = true;
		} else {
			this.removing = true;
		}
	}
	
	public void remove(float stopDuration) {
		if (stopDuration > 0) {
			this.stopDuration = stopDuration;
			this.stopBegan = this.elapsedTime;
			this.stopping = true;
		} else {
			this.removing = true;
		}
	}

	@Override
	public void onFire(CSimulation game) {
		if (this.removing) {
			this.onRemove(game);
			this.setRepeats(false);
			game.unregisterTimer(this);
			return;
		}
		
		this.elapsedTime += WarsmashConstants.SIMULATION_STEP_TIME * 1000;

		float timeRatio = 1;
		if (this.period != 0) {
			timeRatio = (this.elapsedTime - this.startTime) / this.period;
		}
		boolean rolledOver = false;
		if(timeRatio > 1) {
			this.count += (int) timeRatio;
			timeRatio = timeRatio % 1;
			rolledOver = true;
		}
		if (rolledOver) {
			this.startTime = this.elapsedTime - (timeRatio * this.period);
		}
		boolean ending = !this.loop && this.count >= this.totalCount;
		if (ending) {
			timeRatio = 1;
		}
		
		float stopRatio = 1;
		if (this.stopping) {
			float stopFraction = Math.min((this.elapsedTime - this.stopBegan) / this.stopDuration, 1);
			if (stopFraction >= 1) {
				this.removing = true;
				return;
			}
			stopRatio = 1 - stopFraction;
		}
		this.onTick(game, timeRatio, stopRatio);
		
		if (ending) {
			this.removing = true;
		}
	}

	protected abstract void onTick(CSimulation game, float timeRatio, float stopRatio);

	protected abstract void onRemove(CSimulation game);

}
