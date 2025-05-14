package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.integer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackEmptyIntegerList extends ABIntegerListCallback {

	@Override
	public List<Integer> callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		return new ArrayList<>();
	}

}
