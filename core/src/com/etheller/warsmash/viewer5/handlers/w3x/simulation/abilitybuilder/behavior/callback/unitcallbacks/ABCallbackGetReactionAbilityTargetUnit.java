package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetReactionAbilityTargetUnit extends ABUnitCallback {

	@Override
	public CUnit callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		AbilityTarget tar = (AbilityTarget) localStore.get(ABLocalStoreKeys.REACTIONABILITYCASTER+castId);
		return tar.visit(AbilityTargetVisitor.UNIT);
	}

}
