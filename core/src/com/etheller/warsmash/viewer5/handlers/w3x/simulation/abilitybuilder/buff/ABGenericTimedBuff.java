package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;

public class ABGenericTimedBuff extends CBuffTimed {
	private boolean showTimedLifeBar;
	
	public ABGenericTimedBuff(int handleId, War3ID alias, float duration, boolean showTimedLifeBar) {
		super(handleId, alias, duration);
		this.showTimedLifeBar = showTimedLifeBar;
	}

	@Override
	public boolean isTimedLifeBar() {
		return showTimedLifeBar;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
	}

}
