package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABTimedTargetingBuff extends ABGenericTimedBuff {
	public ABTimedTargetingBuff(int handleId, War3ID alias, float duration) {
		super(handleId, alias, duration, false);
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
