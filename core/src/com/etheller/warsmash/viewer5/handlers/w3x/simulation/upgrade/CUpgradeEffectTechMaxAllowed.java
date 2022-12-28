package com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CUpgradeEffectTechMaxAllowed implements CUpgradeEffect {
	private int maxAllowedChange;
	private War3ID rawcode;

	public CUpgradeEffectTechMaxAllowed(int maxAllowedChange, War3ID rawcode) {
		this.maxAllowedChange = maxAllowedChange;
		this.rawcode = rawcode;
	}

	@Override
	public void apply(CSimulation simulation, CUnit unit, int level) {
	}

	@Override
	public void apply(CSimulation simulation, int playerIndex, int level) {
		simulation.getPlayer(playerIndex).setTechtreeMaxAllowed(rawcode, maxAllowedChange);
	}

	@Override
	public void unapply(CSimulation simulation, CUnit unit, int level) {
	}

	@Override
	public void unapply(CSimulation simulation, int playerIndex, int level) {
		simulation.getPlayer(playerIndex).setTechtreeMaxAllowed(rawcode, -maxAllowedChange);
	}
}
