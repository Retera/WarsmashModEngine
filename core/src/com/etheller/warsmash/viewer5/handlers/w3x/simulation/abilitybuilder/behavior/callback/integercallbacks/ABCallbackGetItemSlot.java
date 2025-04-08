package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetItemSlot extends ABIntegerCallback {

	private ABItemCallback item;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		Integer slot = (Integer) localStore.get(ABLocalStoreKeys.ITEMSLOT);
		if (slot != null) {
			return slot;
		}
		
		CItem theItem;
		if (item != null) {
			theItem = item.callback(game, caster, localStore, castId);
		} else {
			theItem = (CItem)localStore.get(ABLocalStoreKeys.ITEM);
		}
		
		if (theItem == null || theItem.getContainedInventory() == null) {
			return null;
		}
		return theItem.getContainedInventory().getSlot(theItem);
	}

}
