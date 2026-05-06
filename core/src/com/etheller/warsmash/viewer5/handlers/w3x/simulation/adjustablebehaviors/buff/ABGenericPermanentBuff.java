package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public abstract class ABGenericPermanentBuff extends ABBuff {

	public ABGenericPermanentBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, boolean showIcon, boolean leveled, boolean positive) {
		super(handleId, alias, alias, localStore, sourceAbility, sourceUnit);
		this.setIconShowing(showIcon);
		this.setLeveled(leveled);
		this.setPositive(positive);
	}

	public ABGenericPermanentBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, boolean leveled, boolean positive) {
		super(handleId, alias, alias, localStore, sourceAbility, sourceUnit);
		this.setLeveled(leveled);
		this.setPositive(positive);
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
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
	}

}
