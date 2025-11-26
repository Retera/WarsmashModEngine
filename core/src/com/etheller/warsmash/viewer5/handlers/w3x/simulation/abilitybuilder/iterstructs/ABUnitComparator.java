package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.iterstructs;

import java.util.Comparator;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABUnitComparator implements Comparator<CUnit> {
	
	private CSimulation game;
	private CUnit caster;
	private Map<String, Object> localStore;
	private int castId;
	private ABIntegerCallback comparison;

	public ABUnitComparator(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId, ABIntegerCallback comparison) {
		this.game = game;
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;
		this.comparison = comparison;
	}

	@Override
	public int compare(CUnit o1, CUnit o2) {
		this.localStore.put(ABLocalStoreKeys.COMPUNIT1, o1);
		this.localStore.put(ABLocalStoreKeys.COMPUNIT2, o2);
		int v = comparison.callback(game, caster, localStore, castId);
		this.localStore.remove(ABLocalStoreKeys.COMPUNIT1);
		this.localStore.remove(ABLocalStoreKeys.COMPUNIT2);
		return v;
	}

}
