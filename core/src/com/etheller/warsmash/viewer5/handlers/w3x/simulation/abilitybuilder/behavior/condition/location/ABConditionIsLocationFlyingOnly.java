package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.location;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsLocationFlyingOnly extends ABCondition {

	private ABLocationCallback location;

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		AbilityPointTarget loc = location.callback(game, caster, localStore, castId);
		return game.getPathingGrid().isPathable(loc.x, loc.y, PathingType.FLYABLE) && !game.getPathingGrid().isPathable(loc.x, loc.y, PathingType.SWIMMABLE) && !game.getPathingGrid().isPathable(loc.x, loc.y, PathingType.WALKABLE);
	}

}
