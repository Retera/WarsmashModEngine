package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABTimedTargetingBuff extends ABGenericTimedBuff {
	public ABTimedTargetingBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, false, false, true, false);
		this.setIconShowing(false);
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
	}

	@Override
	protected void onBuffExpire(CSimulation game, CUnit unit) {
	}

}
