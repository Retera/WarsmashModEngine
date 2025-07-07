package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.game;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionGameplayConstantSmartAbolishMagic extends ABCondition {

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return game.getGameplayConstants().isAbolishMagicSmartDispel();
	}

}
