package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackCatStrings extends ABStringCallback {
	
	private List<ABStringCallback> stringList;
	
	@Override
	public String callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		String output = "";
		for (ABStringCallback callB : stringList) {
			output += callB.callback(game, caster, localStore, castId);
		}
		return output;
	}

}
