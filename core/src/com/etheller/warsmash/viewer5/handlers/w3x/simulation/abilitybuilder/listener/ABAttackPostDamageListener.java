package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPostDamageListener;

public class ABAttackPostDamageListener implements CUnitAttackPostDamageListener {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	public ABAttackPostDamageListener(Map<String, Object> localStore, List<ABAction> actions) {
		this.localStore = localStore;
		this.actions = actions;
	}
	
	@Override
	public void onHit(CSimulation simulation, CUnit attacker, AbilityTarget target, float damage) {
		localStore.put(ABLocalStoreKeys.ATTACKINGUNIT, attacker);
		localStore.put(ABLocalStoreKeys.ATTACKEDUNIT, target);
		localStore.put(ABLocalStoreKeys.TOTALDAMAGEDEALT, damage);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, attacker, localStore);
			}
		}
	}

}
