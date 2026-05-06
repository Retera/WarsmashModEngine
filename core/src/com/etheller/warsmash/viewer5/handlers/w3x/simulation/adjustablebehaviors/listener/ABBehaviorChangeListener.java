package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitBehaviorChangeListener;

public class ABBehaviorChangeListener implements CUnitBehaviorChangeListener {

	private ABLocalDataStore localStore;
	private List<ABAction> actions;

	private int triggerId = ABConstants.STARTING_TRIGGER_ID;
	private boolean useCastId;

	public ABBehaviorChangeListener(ABLocalDataStore localStore, List<ABAction> actions, int castId,
			boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public void onChange(CSimulation game, CUnit unit, CBehavior previousBehavior, CBehavior newBehavior,
			boolean ongoing) {
		if (!this.useCastId) {
			this.triggerId = ABConstants.incrementTriggerId(triggerId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PRECHANGEBEHAVIOR, triggerId), previousBehavior);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.POSTCHANGEBEHAVIOR, triggerId), newBehavior);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BEHAVIORONGOING, triggerId), ongoing);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(unit, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PRECHANGEBEHAVIOR, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.POSTCHANGEBEHAVIOR, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.BEHAVIORONGOING, triggerId));
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
		}
	}

}
