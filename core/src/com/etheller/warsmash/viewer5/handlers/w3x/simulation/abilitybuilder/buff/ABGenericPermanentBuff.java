package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public abstract class ABGenericPermanentBuff extends ABBuff {

	public ABGenericPermanentBuff(int handleId, War3ID alias, boolean showIcon) {
		super(handleId, alias, alias);
		this.setIconShowing(showIcon);
	}
	
	public ABGenericPermanentBuff(int handleId, War3ID alias) {
		super(handleId, alias, alias);
	}

	protected abstract void onBuffAdd(CSimulation game, CUnit unit);

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.onBuffAdd(game, unit);
	}

	protected abstract void onBuffRemove(CSimulation game, CUnit unit);

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		this.onBuffRemove(game, unit);
	}

	@Override
	public float getDurationRemaining(CSimulation game, final CUnit unit) {
		return 0;
	}

	@Override
	public float getDurationMax() {
		return 0;
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
	}


}
