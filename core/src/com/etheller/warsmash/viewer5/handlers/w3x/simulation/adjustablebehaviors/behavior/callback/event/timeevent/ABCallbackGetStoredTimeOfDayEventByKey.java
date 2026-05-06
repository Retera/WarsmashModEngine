package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.event.timeevent;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.event.ABTimeOfDayEvent;

public class ABCallbackGetStoredTimeOfDayEventByKey extends ABTimeOfDayEventCallback {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public ABTimeOfDayEvent callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (instanceValue == null || instanceValue.callback(caster, localStore, castId)) {
			return localStore.get(
					ABLocalStoreKeys.combineUserInstanceKey(key.callback(caster, localStore, castId), castId),
					ABTimeOfDayEvent.class);
		} else {
			return localStore.get(ABLocalStoreKeys.combineUserKey(key.callback(caster, localStore, castId), castId),
					ABTimeOfDayEvent.class);
		}
	}

}
