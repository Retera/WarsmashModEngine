package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalCallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.targetcallbacks.ABTargetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class ABCallbackGetNewBehaviorTarget extends ABTargetCallback {

	@Override
	public AbilityTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CBehavior beh = (CBehavior) localStore.get(ABLocalStoreKeys.POSTCHANGEBEHAVIOR+castId);
		AbilityTarget tar = beh.visit(BehaviorTargetVisitor.INSTANCE);
		
		return tar;
	}

}
