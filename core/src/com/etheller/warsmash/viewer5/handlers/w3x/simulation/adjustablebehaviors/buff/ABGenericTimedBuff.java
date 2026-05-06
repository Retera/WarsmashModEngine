package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public abstract class ABGenericTimedBuff extends ABBuff {
	private final float duration;
	private int currentTick = 0;
	private int expireTick;

	public ABGenericTimedBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, boolean leveled, boolean positive,
			boolean dispellable) {
		super(handleId, alias, alias, localStore, sourceAbility, sourceUnit);
		this.setTimedLifeBar(showTimedLifeBar);
		this.duration = duration;
		this.setLeveled(leveled);
		this.setPositive(positive);
		this.setDispellable(dispellable);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ABGenericTimedBuff other = (ABGenericTimedBuff) obj;
		return this.getAlias() == other.getAlias() && this.getLevel() == other.getLevel() && !this.isStacks()
				&& !other.isStacks();
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
		this.cleanUpUniqueValues();
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
		this.currentTick = 0;
	}
}
