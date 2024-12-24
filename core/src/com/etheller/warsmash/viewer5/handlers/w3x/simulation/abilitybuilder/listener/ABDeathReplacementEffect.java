package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementStacking;

public class ABDeathReplacementEffect implements CUnitDeathReplacementEffect {

	private Map<String, Object> localStore;
	private List<ABAction> actions;
	
	private int triggerId = 0;
	private boolean useCastId;
	
	public ABDeathReplacementEffect(Map<String, Object> localStore, List<ABAction> actions, int castId, boolean useCastId) {
		this.localStore = localStore;
		this.actions = actions;
		this.useCastId = useCastId;
		if (useCastId) {
			this.triggerId = castId;
		}
	}

	@Override
	public CUnitDeathReplacementStacking onDeath(CSimulation simulation, CUnit unit, CUnit killer,
			CUnitDeathReplacementResult result) {
		localStore.put(ABLocalStoreKeys.KILLINGUNIT+triggerId, killer);
		localStore.put(ABLocalStoreKeys.DYINGUNIT+triggerId, unit);
		localStore.put(ABLocalStoreKeys.DEATHRESULT+triggerId, result);
		CUnitDeathReplacementStacking stacking = new CUnitDeathReplacementStacking();
		localStore.put(ABLocalStoreKeys.DEATHSTACKING+triggerId, stacking);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, unit, localStore, triggerId);
			}
		}
		localStore.remove(ABLocalStoreKeys.KILLINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.DYINGUNIT+triggerId);
		localStore.remove(ABLocalStoreKeys.DEATHRESULT+triggerId);
		localStore.remove(ABLocalStoreKeys.DEATHSTACKING+triggerId);
		if (!this.useCastId) {
			this.triggerId++;
		}
		return stacking;
	}

}
