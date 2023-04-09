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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActiveNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActiveNoTargetSimple;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActivePointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActivePointTargetSimple;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActiveSmart;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActiveToggle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActiveUnitTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.CAbilityAbilityBuilderActiveUnitTargetSimple;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;

public class CAbilityTypeAbilityBuilder extends CAbilityType<CAbilityTypeAbilityBuilderLevelData>  {

	private AbilityBuilderConfiguration parser;
	
	public CAbilityTypeAbilityBuilder(War3ID alias, War3ID code, List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration parser) {
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
		case TOGGLE:
			return new CAbilityAbilityBuilderActiveToggle(handleId, getAlias(), getLevelData(), parser, localStore);
		case NORMAL_NOTARGET_SIMPLE:
			return new CAbilityAbilityBuilderActiveNoTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
		case NORMAL_POINTTARGET_SIMPLE:
			return new CAbilityAbilityBuilderActivePointTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
		case NORMAL_UNITTARGET_SIMPLE:
			return new CAbilityAbilityBuilderActiveUnitTargetSimple(handleId, getAlias(), getLevelData(), parser, localStore);
		case NORMAL_NOTARGET:
			return new CAbilityAbilityBuilderActiveNoTarget(handleId, getAlias(), getLevelData(), parser, localStore);
		case NORMAL_POINTTARGET:
			return new CAbilityAbilityBuilderActivePointTarget(handleId, getAlias(), getLevelData(), parser, localStore);
		case NORMAL_UNITTARGET:
		default:
			return new CAbilityAbilityBuilderActiveUnitTarget(handleId, getAlias(), getLevelData(), parser, localStore);
		}
	}

	@Override
	public void setLevel(CSimulation game, CLevelingAbility existingAbility, int level) {
		existingAbility.setLevel(level);
	}

}
