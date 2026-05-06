package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementStacking;

public class ABDeathReplacementEffect implements CUnitDeathReplacementEffect {

	private ABLocalDataStore localStore;
	private List<ABAction> actions;

	private int triggerId = ABConstants.STARTING_TRIGGER_ID;
	private boolean useCastId;

	public ABDeathReplacementEffect(ABLocalDataStore localStore, List<ABAction> actions, int castId,
			boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public CUnitDeathReplacementStacking onDeath(CSimulation simulation, CUnit unit, CUnit killer,
			CUnitDeathReplacementResult result) {
		if (!this.useCastId) {
			this.triggerId = ABConstants.incrementTriggerId(triggerId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.KILLINGUNIT, triggerId), killer);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DYINGUNIT, triggerId), unit);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DEATHRESULT, triggerId), result);
		CUnitDeathReplacementStacking stacking = new CUnitDeathReplacementStacking();
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DEATHSTACKING, triggerId), stacking);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(unit, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.KILLINGUNIT, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DYINGUNIT, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DEATHRESULT, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DEATHSTACKING, triggerId));
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
		}
		return stacking;
	}

}
