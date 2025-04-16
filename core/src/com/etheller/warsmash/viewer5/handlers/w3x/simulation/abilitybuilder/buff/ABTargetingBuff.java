package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public class ABTargetingBuff extends ABBuff {
	public ABTargetingBuff(int handleId, War3ID alias, CAbility sourceAbility, CUnit sourceUnit) {
		super(handleId, alias, alias, sourceAbility, sourceUnit);
		this.setIconShowing(false);
	}

	@Override
	public float getDurationRemaining(CSimulation game, CUnit unit) {
		return 0;
	}

	@Override
	public float getDurationMax() {
		return 0;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
	}

}
