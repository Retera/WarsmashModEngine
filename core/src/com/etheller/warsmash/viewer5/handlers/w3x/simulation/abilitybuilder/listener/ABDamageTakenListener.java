package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackDamageTakenListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABDamageTakenListener implements CUnitAttackDamageTakenListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	public ABDamageTakenListener(Map<String, Object> localStore, List<ABAction> actions) {
		this.localStore = localStore;
		this.actions = actions;
	}
	
	@Override
	public void onDamage(CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged,
			CDamageType damageType, float damage, float bonusDamage, float trueDamage) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT, attacker);
		localStore.put(ABLocalStoreKeys.TARGETUNIT, target);
		localStore.put(ABLocalStoreKeys.DAMAGEISATTACK, isAttack);
		localStore.put(ABLocalStoreKeys.DAMAGEISRANGED, isRanged);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE, damageType);
		localStore.put(ABLocalStoreKeys.BASEDAMAGEDEALT, damage);
		localStore.put(ABLocalStoreKeys.BONUSDAMAGEDEALT, bonusDamage);
		localStore.put(ABLocalStoreKeys.TOTALDAMAGEDEALT, trueDamage);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, target, localStore);
			}
		}
	}

}
