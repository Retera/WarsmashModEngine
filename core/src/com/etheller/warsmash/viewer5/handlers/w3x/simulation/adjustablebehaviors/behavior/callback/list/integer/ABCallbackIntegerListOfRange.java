package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.list.integer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIntegerListOfRange extends ABIntegerListCallback {

	private ABIntegerCallback start;
	private ABIntegerCallback end;

	private ABBooleanCallback inclusiveEnd;

	@Override
	public List<Integer> callback(CUnit caster, ABLocalDataStore localStore, int castId) {
		int theStart = 0;
		if (start != null) {
			theStart = start.callback(caster, localStore, castId);
		}
		if (inclusiveEnd != null && inclusiveEnd.callback(caster, localStore, castId)) {
			return IntStream.rangeClosed(theStart, end.callback(caster, localStore, castId)).boxed()
					.collect(Collectors.toList());
		}
		return IntStream.range(theStart, end.callback(caster, localStore, castId)).boxed()
				.collect(Collectors.toList());
	}

}
