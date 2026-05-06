
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitstate;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs.ABLongCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

public class ABActionCreateStateModBuff implements ABAction {

	private StateModBuffType buffType;
	private ABLongCallback value;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		StateModBuff buff = null;
		if (value != null) {
			buff = new StateModBuff(buffType, value.callback(caster, localStore, castId));
		} else {
			buff = new StateModBuff(buffType, 1);
		}

		localStore.put(ABLocalStoreKeys.LASTCREATEDSMB, buff);
		}
		}