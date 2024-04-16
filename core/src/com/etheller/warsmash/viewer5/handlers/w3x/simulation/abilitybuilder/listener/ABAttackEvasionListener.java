package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEvasionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABAttackEvasionListener implements CUnitAttackEvasionListener {

	private Map<String, Object> localStore;
	private List<ABCondition> conditions;
	
	private int triggerId = 0;
	private boolean useCastId;
	
	public ABAttackEvasionListener(Map<String, Object> localStore, List<ABCondition> conditions, int castId, boolean useCastId) {
		this.localStore = localStore;
		this.conditions = conditions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}
	
	@Override
	public boolean onAttack(CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged,
			CDamageType damageType) {
		boolean evade = false;
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT+triggerId, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT+triggerId, target);
		localStore.put(ABLocalStoreKeys.DAMAGEISATTACK+triggerId, isAttack);
		localStore.put(ABLocalStoreKeys.DAMAGEISRANGED+triggerId, isRanged);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE+triggerId, damageType);
		if (conditions != null) {
			for (ABCondition condition : conditions) {
				evade = evade || condition.evaluate(simulation, target, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKEDUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEISATTACK+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEISRANGED+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGETYPE+triggerId);
		if (!this.useCastId) {
			this.triggerId++;
		}
		return evade;
	}

}
