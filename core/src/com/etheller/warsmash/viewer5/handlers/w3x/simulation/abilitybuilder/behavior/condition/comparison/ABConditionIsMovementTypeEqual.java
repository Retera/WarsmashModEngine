package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.comparison;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABMovementTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsMovementTypeEqual extends ABCondition {

	private ABMovementTypeCallback movementType1;
	private ABMovementTypeCallback movementType2;

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		MovementType lD = movementType1.callback(game, caster, localStore, castId);
		MovementType rD = movementType2.callback(game, caster, localStore, castId);
		if (lD == null) {
			if (rD == null) {
				return true;
			}
			return false;
		}
		return lD.equals(rD);
	}

}
