package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.comparison;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionIsIdEqual extends ABBooleanCallback {

	private ABIDCallback id1;
	private ABIDCallback id2;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		War3ID lA = id1.callback(caster, localStore, castId);
		War3ID rA = id2.callback(caster, localStore, castId);
		if (lA == null) {
			if (rA == null) {
				return true;
			}
			return false;
		}
		return lA.equals(rA);
	}

}
