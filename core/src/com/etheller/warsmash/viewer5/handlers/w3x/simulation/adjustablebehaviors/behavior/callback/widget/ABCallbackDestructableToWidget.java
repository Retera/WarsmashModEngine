package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.widget;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackDestructableToWidget extends ABWidgetCallback {

	private ABDestructableCallback destructable;
	
	@Override
	public CWidget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return destructable.callback(caster, localStore, castId);
	}

}
