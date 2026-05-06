package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetDistanceBetweenLocations extends ABFloatCallback {

	private ABLocationCallback origin;
	private ABLocationCallback target;
	
	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		AbilityPointTarget o = origin.callback(caster, localStore, castId);
		AbilityPointTarget t = target.callback(caster, localStore, castId);
		
		return o.dst(t);
	}

}
