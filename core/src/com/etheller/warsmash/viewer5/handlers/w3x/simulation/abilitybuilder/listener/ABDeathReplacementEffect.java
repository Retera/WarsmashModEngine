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
	
	public ABDeathReplacementEffect(Map<String, Object> localStore, List<ABAction> actions) {
		this.localStore = localStore;
		this.actions = actions;
	}

	@Override
	public CUnitDeathReplacementStacking onDeath(CSimulation simulation, CUnit unit, CUnit killer,
			CUnitDeathReplacementResult result) {
		localStore.put(ABLocalStoreKeys.KILLINGUNIT, killer);
		localStore.put(ABLocalStoreKeys.DYINGUNIT, unit);
		localStore.put(ABLocalStoreKeys.DEATHRESULT, result);
		CUnitDeathReplacementStacking stacking = new CUnitDeathReplacementStacking();
		localStore.put(ABLocalStoreKeys.DEATHSTACKING, stacking);
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(simulation, unit, localStore);
			}
		}
		return stacking;
	}

}
