package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.integer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;

public class ABCallbackIntegerListOfRange extends ABIntegerListCallback {

	private ABIntegerCallback start;
	private ABIntegerCallback end;

	private ABBooleanCallback inclusiveEnd;

	@Override
	public List<Integer> callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		int theStart = 0;
		if (start != null) {
			theStart = start.callback(game, caster, localStore, castId);
		}
		if (inclusiveEnd != null && inclusiveEnd.callback(game, caster, localStore, castId)) {
			return IntStream.rangeClosed(theStart, end.callback(game, caster, localStore, castId)).boxed()
					.collect(Collectors.toList());
		}
		return IntStream.range(theStart, end.callback(game, caster, localStore, castId)).boxed()
				.collect(Collectors.toList());
	}

}
