package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.ability;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionSetAbilityCastRange implements ABAction {

	private ABFloatCallback range;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		AbilityBuilderActiveAbility ability = (AbilityBuilderActiveAbility) localStore.get(ABLocalStoreKeys.TOGGLEDABILITY);
		ability.setCastRange(range.callback(game, caster, localStore, castId));
	}
}
