package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CBuffTimedLife extends CBuffTimed {

	public CBuffTimedLife(int handleId, War3ID alias, float duration) {
		super(handleId, alias, duration);
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {

	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		unit.kill(game);
	}
}
