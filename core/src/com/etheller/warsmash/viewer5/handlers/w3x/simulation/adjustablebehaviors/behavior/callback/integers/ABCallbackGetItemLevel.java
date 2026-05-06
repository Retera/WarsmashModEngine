package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetItemLevel extends ABIntegerCallback {

	private ABItemCallback item;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		Integer slot = localStore.get(ABLocalStoreKeys.ITEMSLOT, Integer.class);
		if (slot != null) {
			return slot;
		}

		CItem theItem;
		if (item != null) {
			theItem = item.callback(caster, localStore, castId);
		} else {
			theItem = localStore.originItem;
		}

		if (theItem == null) {
			return null;
		}
		return theItem.getItemType().getLevel();
	}

}
