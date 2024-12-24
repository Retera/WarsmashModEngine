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
	
	private int triggerId = 0;
	private boolean useCastId;
	
	public ABDamageTakenListener(Map<String, Object> localStore, List<ABAction> actions, int castId, boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}
	
	@Override
	public void onDamage(CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged,
			CDamageType damageType, float damage, float bonusDamage, float trueDamage) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT+triggerId, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT+triggerId, target);
		localStore.put(ABLocalStoreKeys.DAMAGEISATTACK+triggerId, isAttack);
		localStore.put(ABLocalStoreKeys.DAMAGEISRANGED+triggerId, isRanged);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE+triggerId, damageType);
		localStore.put(ABLocalStoreKeys.BASEDAMAGEDEALT+triggerId, damage);
		localStore.put(ABLocalStoreKeys.BONUSDAMAGEDEALT+triggerId, bonusDamage);
		localStore.put(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId, trueDamage);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, target, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKEDUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEISATTACK+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEISRANGED+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGETYPE+triggerId);
		localStore.remove(ABLocalStoreKeys.BASEDAMAGEDEALT+triggerId);
		localStore.remove(ABLocalStoreKeys.BONUSDAMAGEDEALT+triggerId);
		localStore.remove(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId);
		if (!this.useCastId) {
			this.triggerId++;
		}
	}

}
