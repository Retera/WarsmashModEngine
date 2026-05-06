package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackCatStrings extends ABStringCallback {
	
	private List<ABStringCallback> stringList;
	
	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		String output = "";
		for (ABStringCallback callB : stringList) {
			output += callB.callback(caster, localStore, castId);
		}
		return output;
	}

}
