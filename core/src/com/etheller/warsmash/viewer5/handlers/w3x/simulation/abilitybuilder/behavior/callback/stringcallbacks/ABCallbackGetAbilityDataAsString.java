package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.DataFieldLetter;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetAbilityDataAsString extends ABStringCallback {
	
	private DataFieldLetter dataField;

	@SuppressWarnings("unchecked")
	@Override
	public String callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.LEVELDATA);
		int level = (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL);
		
		return levelData.get(level-1).getData().get(dataField.getIndex());
	}

}
