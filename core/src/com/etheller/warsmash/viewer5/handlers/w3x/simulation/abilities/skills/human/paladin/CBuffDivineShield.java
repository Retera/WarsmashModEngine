package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;

public class CBuffDivineShield extends CBuffTimed {
	public CBuffDivineShield(int handleId, War3ID alias, float duration) {
		super(handleId, alias, alias, duration);
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		unit.setInvulnerable(true);
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		unit.setInvulnerable(false);
	}
}
