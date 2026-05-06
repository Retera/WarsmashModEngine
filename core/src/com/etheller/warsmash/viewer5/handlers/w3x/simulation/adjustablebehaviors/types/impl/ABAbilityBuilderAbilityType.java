package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABMapLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;

public class ABAbilityBuilderAbilityType extends CAbilityType<ABAbilityBuilderAbilityTypeLevelData>  {

	private ABAbilityBuilderConfiguration parser;
	private GameObject abilityEditorData;
	
	public ABAbilityBuilderAbilityType(War3ID alias, War3ID code, GameObject abilityEditorData, List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABAbilityBuilderConfiguration parser) {
		super(alias, code, levelData);
		this.parser = parser;
		this.abilityEditorData = abilityEditorData;
	}

	@Override
	public CAbility createAbility(int handleId) {
		ABLocalDataStore localStore = new ABMapLocalDataStore();
		localStore.put(ABLocalStoreKeys.ABILITYEDITORDATA, this.abilityEditorData);
		localStore.put(ABLocalStoreKeys.LEVELDATA, getLevelData());
		localStore.put(ABLocalStoreKeys.ALIAS, getAlias());
		localStore.put(ABLocalStoreKeys.CODE, getCode());
		
		switch (parser.getType()) {
		case PASSIVE:
			return new ABAbilityBuilderPassive(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case HIDDEN:
			return new ABAbilityBuilderNoIcon(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_FLEXTARGET:
			return new ABAbilityBuilderActiveFlexTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_PAIRING:
			return new ABAbilityBuilderActivePairing(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_AUTOTARGET:
			return new ABAbilityBuilderActiveAutoTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_NOTARGET:
			return new ABAbilityBuilderActiveNoTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_POINTTARGET:
			return new ABAbilityBuilderActivePointTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_UNITTARGET:
		default:
			return new ABAbilityBuilderActiveUnitTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		}
	}

	public void setLevel(CSimulation game, CUnit unit, CAbilitySpell existingAbility, int level) {
		existingAbility.setLevel(game, unit, level);
		existingAbility.populate(abilityEditorData, level);
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, CLevelingAbility existingAbility, int level) {
		existingAbility.setLevel(game, unit, level);
	}

}
