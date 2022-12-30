package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CBuffStun extends CBuffTimed {

	public CBuffStun(final int handleId, final War3ID alias, final float duration) {
		super(handleId, alias, duration);
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		unit.setPaused(true);
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.setPaused(false);
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

}
