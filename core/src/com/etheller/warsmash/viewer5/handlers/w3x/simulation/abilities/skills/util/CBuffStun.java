package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

public class CBuffStun extends CBuffTimed {
	private StateModBuff stunBuff;

	public CBuffStun(final int handleId, final War3ID alias, final float duration) {
		super(handleId, alias, alias, duration);
		stunBuff = new StateModBuff(StateModBuffType.STUN, 1);
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		super.onDeath(game, cUnit);
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		unit.addStateModBuff(stunBuff);
		unit.computeUnitState(game, stunBuff.getBuffType());
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.removeStateModBuff(stunBuff);
		unit.computeUnitState(game, stunBuff.getBuffType());
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

}
