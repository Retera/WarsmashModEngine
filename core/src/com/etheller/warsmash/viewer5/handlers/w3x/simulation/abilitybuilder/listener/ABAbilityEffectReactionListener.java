package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.listeners.CUnitAbilityEffectReactionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABAbilityEffectReactionListener implements CUnitAbilityEffectReactionListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int triggerId = 0;
	
	public ABAbilityEffectReactionListener(Map<String, Object> localStore, List<ABAction> actions, int castId) {
		this.localStore = localStore;
		this.actions = actions;
		this.triggerId = castId;
	}
	
	@Override
	public boolean onHit(final CSimulation simulation, CUnit source, CUnit target, CAbility ability) {
		localStore.put(ABLocalStoreKeys.REACTIONALLOWHIT+triggerId, true);
		localStore.put(ABLocalStoreKeys.REACTIONABILITYCASTER+triggerId, source);
		localStore.put(ABLocalStoreKeys.REACTIONABILITYTARGET+triggerId, target);
		localStore.put(ABLocalStoreKeys.REACTIONABILITY+triggerId, ability);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, target, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.REACTIONABILITYCASTER+triggerId);
		localStore.remove(ABLocalStoreKeys.REACTIONABILITYTARGET+triggerId);
		localStore.remove(ABLocalStoreKeys.REACTIONABILITY+triggerId);
		return (boolean) localStore.remove(ABLocalStoreKeys.REACTIONALLOWHIT+triggerId);
	}

}
