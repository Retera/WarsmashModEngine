package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public abstract class ABGenericTimedBuff extends ABBuff {
	private boolean showTimedLifeBar;
	private final float duration;
	private int currentTick = 0;
	private int expireTick;
	
	public ABGenericTimedBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar) {
		super(handleId, alias, alias);
		this.showTimedLifeBar = showTimedLifeBar;
		this.duration = duration;
	}

	@Override
	public boolean isTimedLifeBar() {
		return showTimedLifeBar;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		this.onBuffAdd(game, unit);
		if (this.duration == 0) {
			expireTick = Integer.MAX_VALUE;
		} else {
			final int durationTicks = (int) (this.duration / WarsmashConstants.SIMULATION_STEP_TIME);
			expireTick = durationTicks;
		}
	}

	protected abstract void onBuffAdd(CSimulation game, CUnit unit);

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		this.onBuffRemove(game, unit);
	}
	
	protected abstract void onBuffRemove(CSimulation game, CUnit unit);

	protected abstract void onBuffExpire(CSimulation game, CUnit unit);

	@Override
	public float getDurationMax() {
		return this.duration;
	}

	@Override
	public float getDurationRemaining(final CSimulation game, final CUnit unit) {
		final int remaining = Math.max(0, this.expireTick - this.currentTick);
		return remaining * WarsmashConstants.SIMULATION_STEP_TIME;
	}

	@Override
	public void onTick(final CSimulation game, final CUnit caster) {
		this.currentTick++;
		if (this.currentTick > this.expireTick) {
			this.onBuffExpire(game, caster);
			caster.remove(game, this);
		}
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		cUnit.remove(game, this);
	}
	
	public void updateExpiration(final CSimulation game, final CUnit unit) {
		final int durationTicks = (int) (this.duration / WarsmashConstants.SIMULATION_STEP_TIME);
		expireTick = game.getGameTurnTick() + durationTicks;
	}
}
