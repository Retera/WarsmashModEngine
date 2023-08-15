package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.DataFieldLetter;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetParentAbilityDataAsFloat extends ABFloatCallback {
	
	private DataFieldLetter dataField;

	@SuppressWarnings("unchecked")
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.PARENTLEVELDATA);
		int parentLevel = (int) ((Map<String, Object>)localStore.get(ABLocalStoreKeys.PARENTLOCALSTORE)).get(ABLocalStoreKeys.CURRENTLEVEL);
		
		String data = levelData.get(parentLevel-1).getData().get(dataField.getIndex());
		
		return Float.parseFloat(data);
	}

}
