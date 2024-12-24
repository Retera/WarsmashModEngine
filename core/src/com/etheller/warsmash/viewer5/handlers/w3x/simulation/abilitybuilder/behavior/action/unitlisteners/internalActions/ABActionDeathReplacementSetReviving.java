
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalActions;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementStacking;

public class ABActionDeathReplacementSetReviving implements ABAction {

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		CUnitDeathReplacementResult result = (CUnitDeathReplacementResult)localStore.get(ABLocalStoreKeys.DEATHRESULT+castId);
		CUnitDeathReplacementStacking stacking = (CUnitDeathReplacementStacking)localStore.get(ABLocalStoreKeys.DEATHSTACKING+castId);
		
		stacking.setAllowStacking(false);
		stacking.setAllowSamePriorityStacking(false);
		result.setReviving(true);
		caster.setFalseDeath(true);
	}
}