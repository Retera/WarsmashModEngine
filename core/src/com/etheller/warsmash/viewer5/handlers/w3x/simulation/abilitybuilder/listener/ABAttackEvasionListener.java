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
	
	public ABAttackEvasionListener(Map<String, Object> localStore, List<ABCondition> conditions) {
		this.localStore = localStore;
		this.conditions = conditions;
	}
	
	@Override
	public boolean onAttack(CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged,
			CDamageType damageType) {
		boolean evade = false;
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT, attacker);
		localStore.put(ABLocalStoreKeys.TARGETUNIT, target);
		localStore.put(ABLocalStoreKeys.DAMAGEISATTACK, isAttack);
		localStore.put(ABLocalStoreKeys.DAMAGEISRANGED, isRanged);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE, damageType);
		if (conditions != null) {
			for (ABCondition condition : conditions) {
				evade = evade || condition.evaluate(simulation, target, localStore);
			}
		}
		return evade;
	}

}
