package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class ABAttackPreDamageListener implements CUnitAttackPreDamageListener {

	private ABLocalDataStore localStore;
	private List<ABAction> actions;

	private int triggerId = ABConstants.STARTING_TRIGGER_ID;
	private boolean useCastId;

	public ABAttackPreDamageListener(ABLocalDataStore localStore, List<ABAction> actions, int castId,
			boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public void onAttack(CSimulation simulation, AbilityTarget target, AbilityPointTarget attackImpactLocation,
			CUnitAttack attack, CUnitAttackSettings settings, CDamageCalculation calc) {
		if (!this.useCastId) {
			this.triggerId = ABConstants.incrementTriggerId(triggerId);
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId),
					this.localStore.originAbility.getLevel());
		}
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId), calc.getSource());
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId), target);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKIMPACTLOCATION, triggerId),
				attackImpactLocation);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, triggerId), attack);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKSETTINGS, triggerId), settings);
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, triggerId), calc);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(calc.getSource(), localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKINGUNIT, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKIMPACTLOCATION, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKSETTINGS, triggerId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, triggerId));
		if (!this.useCastId) {
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, this.triggerId));
		}
	}

}
