package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitBehaviorChangeListener;

public class ABBehaviorChangeListener implements CUnitBehaviorChangeListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int triggerId = 0;
	private boolean useCastId;
	
	public ABBehaviorChangeListener(Map<String, Object> localStore, List<ABAction> actions, int castId, boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}
	
	@Override
	public void onChange(CSimulation game, CUnit unit, CBehavior previousBehavior, CBehavior newBehavior, boolean ongoing) {
		localStore.put(ABLocalStoreKeys.PRECHANGEBEHAVIOR+triggerId, previousBehavior);
		localStore.put(ABLocalStoreKeys.POSTCHANGEBEHAVIOR+triggerId, newBehavior);
		localStore.put(ABLocalStoreKeys.BEHAVIORONGOING+triggerId, ongoing);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(game, unit, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.PRECHANGEBEHAVIOR+triggerId);
		localStore.remove(ABLocalStoreKeys.POSTCHANGEBEHAVIOR+triggerId);
		localStore.remove(ABLocalStoreKeys.BEHAVIORONGOING+triggerId);
		if (!this.useCastId) {
			this.triggerId++;
		}
	}

}
