
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalActions;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackEffectListenerStacking;

public class ABActionSetPreDamageStacking implements ABAction {

	private ABBooleanCallback allowStacking;
	private ABBooleanCallback allowSamePriorityStacking;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		CUnitAttackEffectListenerStacking stacking = (CUnitAttackEffectListenerStacking) localStore.get(ABLocalStoreKeys.PREDAMAGESTACKING);
		
		stacking.setAllowStacking(allowStacking.callback(game, caster, localStore));
		stacking.setAllowSamePriorityStacking(allowSamePriorityStacking.callback(game, caster, localStore));
	}
}