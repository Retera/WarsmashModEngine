package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.location;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionIsLocationDeepWater extends ABBooleanCallback {

	private ABLocationCallback location;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		AbilityPointTarget loc = location.callback(caster, localStore, castId);
		return localStore.game.getPathingGrid().isPathable(loc.x, loc.y, PathingType.SWIMMABLE)
				&& !localStore.game.getPathingGrid().isPathable(loc.x, loc.y, PathingType.WALKABLE);
	}

}
