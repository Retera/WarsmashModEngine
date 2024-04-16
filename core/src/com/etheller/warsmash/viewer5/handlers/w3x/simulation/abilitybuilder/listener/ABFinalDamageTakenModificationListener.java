package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackFinalDamageTakenModificationListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABFinalDamageTakenModificationListener implements CUnitAttackFinalDamageTakenModificationListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int triggerId = 0;
	private boolean useCastId;
	
	public ABFinalDamageTakenModificationListener(Map<String, Object> localStore, List<ABAction> actions, int castId, boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}
	
	@Override
	public float onDamage(CSimulation simulation, CUnit attacker,
			CUnit target, boolean isAttack, boolean isRanged, CAttackType attackType, CDamageType damageType,
			float previousDamage) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT+triggerId, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT+triggerId, target);
		localStore.put(ABLocalStoreKeys.DAMAGEISATTACK+triggerId, isAttack);
		localStore.put(ABLocalStoreKeys.DAMAGEISRANGED+triggerId, isRanged);
		localStore.put(ABLocalStoreKeys.ATTACKTYPE+triggerId, attackType);
		localStore.put(ABLocalStoreKeys.DAMAGETYPE+triggerId, damageType);
		localStore.put(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId, previousDamage);
		localStore.put(ABLocalStoreKeys.DAMAGEMODRESULT+triggerId, previousDamage);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, target, localStore, triggerId);
			}
		}
		float finalDamage = (float) localStore.get(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKEDUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEISATTACK+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEISRANGED+triggerId);
		localStore.remove(ABLocalStoreKeys.ATTACKTYPE+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGETYPE+triggerId);
		localStore.remove(ABLocalStoreKeys.TOTALDAMAGEDEALT+triggerId);
		localStore.remove(ABLocalStoreKeys.DAMAGEMODRESULT+triggerId);
		if (!this.useCastId) {
			this.triggerId++;
		}
		return finalDamage;
	}

}
