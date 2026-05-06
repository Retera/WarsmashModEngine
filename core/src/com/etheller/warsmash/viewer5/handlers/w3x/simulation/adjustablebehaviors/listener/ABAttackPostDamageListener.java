package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;

public class ABAttackPostDamageListener implements CUnitAttackPostDamageListener {

	private ABLocalDataStore localStore;
	private ABIntegerCallback priority;
	private List<ABAction> actions;

	private int triggerId = ABConstants.STARTING_TRIGGER_PRIORITY_ID;
	private boolean useCastId;

	public ABAttackPostDamageListener(ABLocalDataStore localStore, ABIntegerCallback priority, List<ABAction> actions,
			int castId, boolean useCastId) {
		this.localStore = localStore;
		this.priority = priority;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public int getPriority(CSimulation simulation, CUnit attacker, AbilityTarget target, CUnitAttack attack) {
		if (priority == null) {
			return 0;
		}
		if (!this.useCastId) {
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, this.triggerId), attacker);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, this.triggerId), target);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, this.triggerId), attack);
		int prio = this.priority.callback(attacker, this.localStore, this.triggerId);
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, this.triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, this.triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, this.triggerId));
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
		}
		return prio;
	}

	@Override
	public void onHit(CSimulation simulation, AbilityTarget target, CUnitAttack attack, CDamageCalculation damage) {
		if (!this.useCastId) {
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId), damage.getSource());
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId), target);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, triggerId), damage);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, this.triggerId), attack);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(damage.getSource(), localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, this.triggerId));
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
			this.triggerId = ABConstants.incrementTriggerId(triggerId);
		}
	}

}
