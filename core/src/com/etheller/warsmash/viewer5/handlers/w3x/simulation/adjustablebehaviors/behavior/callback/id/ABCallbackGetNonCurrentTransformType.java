package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetNonCurrentTransformType extends ABIDCallback {

	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;

	@Override
	public War3ID callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit u1 = caster;
		if (unit != null) {
			u1 = unit.callback(caster, localStore, castId);
		}
		War3ID baseId = baseUnitId.callback(caster, localStore, castId);
		War3ID altId = alternateUnitId.callback(caster, localStore, castId);

		if (altId == null || u1.getTypeId().equals(altId)) {
			return baseId;
		} else {
			return altId;
		}
	}

}
