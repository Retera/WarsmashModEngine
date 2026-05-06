package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABTimer extends CTimer {
	private CUnit caster;
	private ABLocalDataStore localStore;
	private List<ABAction> actions;
	
	private int castId = 0;
	private boolean alive = true;

	public ABTimer(CUnit caster, ABLocalDataStore localStore, List<ABAction> actions, final int castId) {
		super();
		this.caster = caster;
		this.localStore = localStore;
		this.actions = actions;
		this.castId = castId;
	}

	public void kill(CSimulation simulation) {
		simulation.unregisterTimer(this);
		this.alive  = false;
		this.caster = null;
		this.localStore = null;
		this.actions = null;
	}
	
	public void onFire(CSimulation simulation) {
		if (alive) {
			localStore.put(ABLocalStoreKeys.FIRINGTIMER, this);
			if (actions != null) {
				for (ABAction action : actions) {
					action.runAction(caster, localStore, castId);
				}
			}
			if (localStore != null)
				localStore.remove(ABLocalStoreKeys.FIRINGTIMER);
		}
	}
	
}
