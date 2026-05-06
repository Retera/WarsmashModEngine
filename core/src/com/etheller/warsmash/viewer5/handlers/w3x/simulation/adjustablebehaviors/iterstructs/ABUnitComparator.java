package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs;

import java.util.Comparator;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABUnitComparator implements Comparator<CUnit> {

	private CUnit caster;
	private ABLocalDataStore localStore;
	private int castId;
	private ABIntegerCallback comparison;

	public ABUnitComparator(final CUnit caster, final ABLocalDataStore localStore, final int castId,
			ABIntegerCallback comparison) {
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;
		this.comparison = comparison;
	}

	@Override
	public int compare(CUnit o1, CUnit o2) {
		this.localStore.put(ABLocalStoreKeys.COMPUNIT1, o1);
		this.localStore.put(ABLocalStoreKeys.COMPUNIT2, o2);
		int v = comparison.callback(caster, localStore, castId);
		this.localStore.remove(ABLocalStoreKeys.COMPUNIT1);
		this.localStore.remove(ABLocalStoreKeys.COMPUNIT2);
		return v;
	}

}
