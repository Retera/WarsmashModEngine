package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABDelayTimerTimer extends CTimer {

	private CTimer timer;
	ABLocalDataStore localStore;

	public ABDelayTimerTimer(CTimer timer, ABLocalDataStore localStore, float delay) {
		super();
		this.timer = timer;
		this.localStore = localStore;
		this.setRepeats(false);
		this.setTimeoutTime(delay);
	}

	public void onFire(CSimulation game) {
		localStore.put(ABLocalStoreKeys.ACTIVE_ALTITUDE_ADJUSTMENT, timer);
		timer.start(game);
	}

}
