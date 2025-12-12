package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface CPausedTickingBuff {
	public void onTick(CSimulation game, CUnit unit);
}
