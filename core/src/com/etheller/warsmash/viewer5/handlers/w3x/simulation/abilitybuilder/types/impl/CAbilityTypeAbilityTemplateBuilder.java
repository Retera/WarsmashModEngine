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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template.CAbilityAbilityBuilderAuraTemplate;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template.CAbilityAbilityBuilderSimpleAuraTemplate;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template.CAbilityAbilityBuilderStatAuraTemplate;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.template.CAbilityAbilityBuilderStatPassiveTemplate;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;

public class CAbilityTypeAbilityTemplateBuilder extends CAbilityType<CAbilityTypeAbilityBuilderLevelData>  {

	private AbilityBuilderParser parser;
	private GameObject abilityEditorData;
	
	public CAbilityTypeAbilityTemplateBuilder(War3ID alias, War3ID code, GameObject abilityEditorData, List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderParser parser) {
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
		
		switch (parser.getTemplateType()) {
		case PASSIVE_STATS:
			return new CAbilityAbilityBuilderStatPassiveTemplate(handleId, getCode(), getAlias(), getLevelData(), localStore, parser.getStatBuffsFromDataFields());
		case AURA_STATS:
			return new CAbilityAbilityBuilderStatAuraTemplate(handleId, getCode(), getAlias(), getLevelData(), localStore, parser.getStatBuffsFromDataFields(), parser.getMeleeRangeTargetOverride());
		case AURA_SIMPLE:
			return new CAbilityAbilityBuilderSimpleAuraTemplate(handleId, getCode(), getAlias(), getLevelData(), localStore, parser.getAbilityIdsToAddPerLevel(), parser.getLevellingAbilityIdsToAdd());
		case AURA:
		default:
			return new CAbilityAbilityBuilderAuraTemplate(handleId, getCode(), getAlias(), getLevelData(), localStore, parser.getAddToAuraActions(), parser.getUpdateAuraLevelActions(), parser.getRemoveFromAuraActions());
		}
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, CLevelingAbility existingAbility, int level) {
		existingAbility.setLevel(game, unit, level);
	}

}
