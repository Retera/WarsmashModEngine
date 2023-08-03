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

	public ABTimer(CUnit caster, Map<String, Object> localStore, List<ABAction> actions) {
		super();
		this.caster = caster;
		this.localStore = localStore;
		this.actions = actions;
	}

	
	private void onFire(CSimulation simulation) {
		localStore.put(ABLocalStoreKeys.FIRINGTIMER, this);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, caster, localStore);
			}
		}
	}
	
	@Override
	public void fire(final CSimulation simulation) {
		onFire(simulation);
		super.fire(simulation);
	}
	
	@Override
	public void onFire() {
		//Intentionally Empty
	}

}
