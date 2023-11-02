package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABTimer extends CTimer {
	private CUnit caster;
	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int castId = 0;

	public ABTimer(CUnit caster, Map<String, Object> localStore, List<ABAction> actions, final int castId) {
		super();
		this.caster = caster;
		this.localStore = localStore;
		this.actions = actions;
		this.castId = castId;
	}

	
	public void onFire(CSimulation simulation) {
		localStore.put(ABLocalStoreKeys.FIRINGTIMER, this);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, caster, localStore, castId);
			}
		}
	}
	
}
