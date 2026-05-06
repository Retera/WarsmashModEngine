package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.listeners.CUnitAbilityEffectReactionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABAbilityEffectReactionListener implements CUnitAbilityEffectReactionListener {

	private ABLocalDataStore localStore;
	private List<ABAction> actions;

	private int triggerId = ABConstants.STARTING_TRIGGER_ID;
	private boolean useCastId;

	public ABAbilityEffectReactionListener(ABLocalDataStore localStore, List<ABAction> actions, int castId,
			boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public boolean onHit(final CSimulation simulation, CUnit source, CUnit target, CAbility ability) {
		if (!this.useCastId) {
			this.triggerId = ABConstants.incrementTriggerId(triggerId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONALLOWHIT, triggerId), true);
		if (actions != null) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONABILITYCASTER, triggerId), source);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONABILITYTARGET, triggerId), target);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONABILITY, triggerId), ability);
			for (ABAction action : actions) {
				action.runAction(target, localStore, triggerId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONABILITYCASTER, triggerId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONABILITYTARGET, triggerId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONABILITY, triggerId));
		}
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
		}
		return (boolean) localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONALLOWHIT, triggerId));
	}

}
