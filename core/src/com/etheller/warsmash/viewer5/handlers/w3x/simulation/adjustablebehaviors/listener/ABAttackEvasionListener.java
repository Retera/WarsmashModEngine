package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEvasionListener;

public class ABAttackEvasionListener implements CUnitAttackEvasionListener {

	private ABLocalDataStore localStore;
	private List<ABBooleanCallback> conditions;

	private int triggerId = ABConstants.STARTING_TRIGGER_ID;
	private boolean useCastId;

	public ABAttackEvasionListener(ABLocalDataStore localStore, List<ABBooleanCallback> conditions, int castId,
			boolean useCastId) {
		this.localStore = localStore;
		this.conditions = conditions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public boolean onAttack(CSimulation simulation, CUnit target, CDamageCalculation damage) {
		boolean evade = false;
		if (conditions != null) {
			if (!this.useCastId) {
				this.triggerId = ABConstants.incrementTriggerId(triggerId);
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
						this.localStore.originAbility.getLevel());
			}
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId), damage.getSource());
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId), target);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, triggerId), damage);
			for (ABBooleanCallback condition : conditions) {
				evade = evade || condition.callback(target, localStore, triggerId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, triggerId));
			if (!this.useCastId) {
				this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
			}
		}
		return evade;
	}

}
