package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;

public class CAbilityTypeAbilityBuilder extends CAbilityType<CAbilityTypeAbilityBuilderLevelData>  {

	private AbilityBuilderConfiguration parser;
	private GameObject abilityEditorData;
	
	public CAbilityTypeAbilityBuilder(War3ID alias, War3ID code, GameObject abilityEditorData, List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration parser) {
		super(alias, code, levelData);
		this.parser = parser;
		this.abilityEditorData = abilityEditorData;
	}

	@Override
	public CAbility createAbility(int handleId) {
		Map<String, Object> localStore = new HashMap<>();
		localStore.put(ABLocalStoreKeys.ABILITYEDITORDATA, this.abilityEditorData);
		localStore.put(ABLocalStoreKeys.LEVELDATA, getLevelData());
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, 1);
		localStore.put(ABLocalStoreKeys.ALIAS, getAlias());
		localStore.put(ABLocalStoreKeys.CODE, getCode());
		CAbilitySpell ability;
		
		switch (parser.getType()) {
		case PASSIVE:
			return new CAbilityAbilityBuilderPassive(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case HIDDEN:
			return new CAbilityAbilityBuilderNoIcon(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case TOGGLE:
			return new CAbilityAbilityBuilderActiveToggle(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_NOTARGET_SIMPLE:
			ability = new CAbilityAbilityBuilderActiveNoTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
			ability.populate(this.abilityEditorData, 1);
			return ability;
		case NORMAL_POINTTARGET_SIMPLE:
			ability = new CAbilityAbilityBuilderActivePointTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
			ability.populate(this.abilityEditorData, 1);
			return ability;
		case NORMAL_UNITTARGET_SIMPLE:
			ability = new CAbilityAbilityBuilderActiveUnitTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
			ability.populate(this.abilityEditorData, 1);
			return ability;
		case NORMAL_FLEXTARGET_SIMPLE:
			ability = new CAbilityAbilityBuilderActiveFlexTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
			ability.populate(this.abilityEditorData, 1);
			return ability;
		case NORMAL_FLEXTARGET:
			return new CAbilityAbilityBuilderActiveFlexTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_PAIRING:
			return new CAbilityAbilityBuilderActivePairing(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_AUTOTARGET:
			return new CAbilityAbilityBuilderActiveAutoTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_NOTARGET:
			return new CAbilityAbilityBuilderActiveNoTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_POINTTARGET:
			return new CAbilityAbilityBuilderActivePointTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
		case NORMAL_UNITTARGET:
		default:
			return new CAbilityAbilityBuilderActiveUnitTarget(handleId, getCode(), getAlias(), getLevelData(), parser, localStore);
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
