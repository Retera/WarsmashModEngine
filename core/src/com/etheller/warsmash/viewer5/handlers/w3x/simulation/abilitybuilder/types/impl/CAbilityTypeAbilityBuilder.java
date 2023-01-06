package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderNoIcon;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderPassive;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderSingleIconActive;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderSingleIconNoSmartActive;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;

public class CAbilityTypeAbilityBuilder extends CAbilityType<CAbilityTypeAbilityBuilderLevelData>  {

	private AbilityBuilderParser parser;
	
	public CAbilityTypeAbilityBuilder(War3ID alias, War3ID code, List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderParser parser) {
		super(alias, code, levelData);
		this.parser = parser;
	}

	@Override
	public CAbility createAbility(int handleId) {
		Map<String, Object> localStore = new HashMap<>();
		localStore.put(ABLocalStoreKeys.LEVELDATA, getLevelData());
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, 1);
		localStore.put(ABLocalStoreKeys.ALIAS, getAlias());
		
		switch (parser.getType()) {
		case BUFF:
			return new CAbilityAbilityBuilderBuff(handleId, getAlias(), getLevelData(), parser, localStore);
		case PASSIVE:
			return new CAbilityAbilityBuilderPassive(handleId, getAlias(), getLevelData(), parser, localStore);
		case HIDDEN:
			return new CAbilityAbilityBuilderNoIcon(handleId, getAlias(), getLevelData(), parser, localStore);
		case SMART:
			return new CAbilityAbilityBuilderSingleIconActive(handleId, getAlias(), getLevelData(), parser, localStore);
		case TOGGLE:
		case NORMAL_NOTARGET:
		case NORMAL_POINTTARGET:
		case NORMAL_UNITTARGET:
		default:
			return new CAbilityAbilityBuilderSingleIconNoSmartActive(handleId, getAlias(), getLevelData(), parser, localStore);
		}
	}

	@Override
	public void setLevel(CSimulation game, CLevelingAbility existingAbility, int level) {
		existingAbility.setLevel(level);
	}

}
