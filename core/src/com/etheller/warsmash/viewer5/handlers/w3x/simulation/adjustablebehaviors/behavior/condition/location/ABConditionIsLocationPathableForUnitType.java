package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.location;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionIsLocationPathableForUnitType extends ABBooleanCallback {

	ABLocationCallback location;
	ABIDCallback unitType;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, int castId) {
		War3ID uType = unitType.callback(caster, localStore, castId);
		AbilityPointTarget loc = location.callback(caster, localStore, castId);
		return localStore.game.getPathingGrid().isPathable(loc.x, loc.y,
				localStore.game.getUnitData().getUnitType(uType).getMovementType());
	}

}
