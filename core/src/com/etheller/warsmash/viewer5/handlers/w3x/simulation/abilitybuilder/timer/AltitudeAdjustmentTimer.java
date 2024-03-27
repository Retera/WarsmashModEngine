package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class AltitudeAdjustmentTimer extends CTimer {
	private CUnit unit;
	private float heightInc;
	private int durTicks;
	private int endTick = 0;

	public AltitudeAdjustmentTimer(CSimulation game, CUnit unit, float targetHeight, float duration) {
		super();
		this.unit = unit;
		durTicks = Math.round(duration / WarsmashConstants.SIMULATION_STEP_TIME);
		if (durTicks == 0) {
			durTicks = 1;
		}
		this.heightInc = (targetHeight - unit.getFlyHeight()) / durTicks;
		if (durTicks > 1) {
			this.setRepeats(true);
		}
		this.setTimeoutTime(0f);
	}
	
	@Override
	public void start(CSimulation game) {
		if (endTick == 0) {
			this.endTick = game.getGameTurnTick() + durTicks - 1;
		}
		super.start(game);
	}

	public void onFire(CSimulation game) {
		if (this.unit.isDead()) {
			this.setRepeats(false);
			return;
		}
		this.unit.setFlyHeight(this.unit.getFlyHeight() + this.heightInc);
		if(game.getGameTurnTick() >= endTick) {
			this.setRepeats(false);
		}
	}
	
}
