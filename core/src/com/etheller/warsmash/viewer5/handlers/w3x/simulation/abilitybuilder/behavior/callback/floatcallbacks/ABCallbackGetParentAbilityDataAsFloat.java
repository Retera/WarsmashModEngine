package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetParentAbilityDataAsFloat extends ABFloatCallback {
	
	private ABIntegerCallback dataField;

	@SuppressWarnings("unchecked")
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.PARENTLEVELDATA);
		int parentLevel = (int) ((Map<String, Object>)localStore.get(ABLocalStoreKeys.PARENTLOCALSTORE)).get(ABLocalStoreKeys.CURRENTLEVEL);
		
		String data = levelData.get(parentLevel-1).getData().get(dataField.callback(game, caster, localStore));
		
		return Float.parseFloat(data);
	}

}
