package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.listeners.CUnitAttackProjReactionListener;

public class ABAttackProjReactionListener implements CUnitAttackProjReactionListener {

	private ABLocalDataStore localStore;
	private List<ABAction> actions;

	private int triggerId = ABConstants.STARTING_TRIGGER_ID;
	private boolean useCastId;

	public ABAttackProjReactionListener(ABLocalDataStore localStore, List<ABAction> actions, int castId,
			boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public boolean onHit(final CSimulation simulation, CUnit source, CUnit target, CAttackProjectile projectile) {
		if (!this.useCastId) {
			this.triggerId = ABConstants.incrementTriggerId(triggerId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONALLOWHIT, triggerId), true);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId), source);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId), target);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKPROJ, triggerId), projectile);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(target, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKPROJ, triggerId));
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
		}
		return (boolean) localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONALLOWHIT, triggerId));
	}

}
