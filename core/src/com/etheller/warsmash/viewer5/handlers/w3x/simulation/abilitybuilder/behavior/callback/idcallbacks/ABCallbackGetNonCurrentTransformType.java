package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackGetNonCurrentTransformType extends ABIDCallback {
	
	private ABUnitCallback unit;
	private ABIDCallback baseUnitId;
	private ABIDCallback alternateUnitId;
	
	@Override
	public War3ID callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit u1 = caster;
		if (unit != null) {
			u1 = unit.callback(game, caster, localStore, castId);
		}
		War3ID baseId = baseUnitId.callback(game, caster, localStore, castId);
		War3ID altId = alternateUnitId.callback(game, caster, localStore, castId);
		
		if (altId == null || u1.getTypeId().equals(altId)) {
			return baseId;
		} else {
			return altId;
		}
	}

}
