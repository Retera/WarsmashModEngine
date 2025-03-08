package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unittype;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsLocationPathableForUnitType extends ABCondition {

	ABLocationCallback location;
	ABIDCallback unitType;
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		War3ID uType = unitType.callback(game, caster, localStore, castId);
		AbilityPointTarget loc = location.callback(game, caster, localStore, castId);
		return game.getPathingGrid().isPathable(loc.x, loc.y, game.getUnitData().getUnitType(uType).getMovementType());
	}

}
