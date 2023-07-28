package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackDamageTakenModificationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackDamageTakenModificationListenerDamageModResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABDamageTakenModificationListener implements CUnitAttackDamageTakenModificationListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	public ABDamageTakenModificationListener(Map<String, Object> localStore, List<ABAction> actions) {
		this.localStore = localStore;
		this.actions = actions;
	}
	
	@Override
	public CUnitAttackDamageTakenModificationListenerDamageModResult onDamage(CSimulation simulation, CUnit attacker,
			CUnit target, boolean isAttack, boolean isRanged, CAttackType attackType, CDamageType damageType,
			CUnitAttackDamageTakenModificationListenerDamageModResult previousDamage) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT, attacker);
		localStore.put(ABLocalStoreKeys.TARGETUNIT, target);
		localStore.put(ABLocalStoreKeys.DAMAGEISATTACK, isAttack);
		localStore.put(ABLocalStoreKeys.DAMAGEISRANGED, isRanged);
		localStore.put(ABLocalStoreKeys.ATTACKTYPE, attackType);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE, damageType);
		localStore.put(ABLocalStoreKeys.BASEDAMAGEDEALT, previousDamage.getBaseDamage());
		localStore.put(ABLocalStoreKeys.BONUSDAMAGEDEALT, previousDamage.getBonusDamage());
		localStore.put(ABLocalStoreKeys.DAMAGEMODRESULT, previousDamage);
		System.err.println("In the listener (Rng:"+isRanged+",");
		if (actions != null) {
			System.err.println("There are " + actions.size() + " actions");
			for (ABAction action : actions) {
				action.runAction(simulation, target, localStore);
			}
		}
		return previousDamage;
	}

}
