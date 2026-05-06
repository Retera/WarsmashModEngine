package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackDetectionDropdownConversion extends ABIntegerCallback {

	private ABIntegerCallback value;
	
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		int val = value.callback(caster, localStore, castId);
		switch(val) {
		case 1:
			return 1;
		case 2:
			return 2;
		case 3:
			return 127;
		default:
				
		}
		return 0;
	}

}
