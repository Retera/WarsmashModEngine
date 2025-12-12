package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.ability;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionDoesAbilityHaveUniqueFlag extends ABCondition {

	ABAbilityCallback ability;
	ABStringCallback flag;
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CAbility theAbility = ability.callback(game, caster, localStore, castId);
		return theAbility.hasUniqueFlag(flag.callback(game, caster, localStore, castId));
	}

}
