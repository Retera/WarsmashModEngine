package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;

public class ABCallbackEmptyLocationList extends ABLocationListCallback {

	@Override
	public List<AbilityPointTarget> callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		return new ArrayList<>();
	}

}
