package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class ABCallbackGetParentAbilityDataAsBoolean extends ABBooleanCallback {
	
	private ABIntegerCallback dataField;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		List<CAbilityTypeAbilityBuilderLevelData>  levelData = (List<CAbilityTypeAbilityBuilderLevelData>) localStore.get(ABLocalStoreKeys.PARENTLEVELDATA);
		int parentLevel = (int) ((Map<String, Object>)localStore.get(ABLocalStoreKeys.PARENTLOCALSTORE)).get(ABLocalStoreKeys.CURRENTLEVEL);
		
		String data = levelData.get(parentLevel-1).getData().get(dataField.callback(game, caster, localStore));
		int parsedData = Integer.parseInt(data);
		return parsedData == 1;
	}

}
