package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public class CAbilityAbilityBuilderBuff extends CAbilityAbilityBuilderNoIcon {

	public CAbilityAbilityBuilderBuff(int handleId, War3ID code, War3ID alias, List<CAbilityTypeAbilityBuilderLevelData> levelData,
			AbilityBuilderConfiguration config, Map<String, Object> localStore) {
		super(handleId, code, alias, levelData, config, localStore);
	}

	public void setParentLevelData(List<CAbilityTypeAbilityBuilderLevelData> parentLevelData) {
		this.localStore.put(ABLocalStoreKeys.PARENTLEVELDATA, parentLevelData);
	}

	public void setParentCaster(CUnit parentCaster) {
		this.localStore.put(ABLocalStoreKeys.PARENTCASTER, parentCaster);
	}

	public void setParentLocalStore(Map<String, Object> parentLocalStore) {
		this.localStore.put(ABLocalStoreKeys.PARENTLOCALSTORE, parentLocalStore);
	}

}
