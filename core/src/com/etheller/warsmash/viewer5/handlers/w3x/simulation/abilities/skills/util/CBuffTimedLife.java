package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CBuffTimedLife extends CBuffTimed {

	public CBuffTimedLife(final int handleId, final War3ID alias, final float duration) {
		super(handleId, alias, duration);
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.kill(game);
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		// do not remove this (avoid default behavior from CBuffTimed)
		// TODO maybe do this in a smarter way later
	}

	@Override
	public boolean isTimedLifeBar() {
		return true;
	}
}
