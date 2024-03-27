package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetAbilityCastTimeAsInteger extends ABIntegerCallback {

	
	@SuppressWarnings("unchecked")
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA);
		return (int) levelData.get(((int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL))-1).getCastTime();
	}

}
