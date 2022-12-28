package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;

public class CAbilityTypeDefinitionSpellBase implements CAbilityTypeDefinition {
	private AbilityConstructor abilityConstructor;

	public CAbilityTypeDefinitionSpellBase(AbilityConstructor abilityConstructor) {
		this.abilityConstructor = abilityConstructor;
	}

	@Override
	public CAbilityType<?> createAbilityType(War3ID alias, MutableGameObject abilityEditorData) {
		List<CAbilityTypeLevelData> emptyLevelDatas = new ArrayList<>();
		int levels = abilityEditorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
		for (int i = 0; i < levels; i++) {
			// NOTE: for now, size of this list is getting used for ability's max level for
			// heroes
			emptyLevelDatas.add(null);
		}
		return new CAbilityType<CAbilityTypeLevelData>(alias, abilityEditorData.getCode(), emptyLevelDatas) {
			@Override
			public CAbility createAbility(int handleId) {
				CAbilitySpellBase spellAbility = abilityConstructor.create(handleId, getAlias());
				spellAbility.populate(abilityEditorData, 1);
				return spellAbility;
			}

			@Override
			public void setLevel(CSimulation game, CLevelingAbility existingAbility, int level) {
				existingAbility.setLevel(level);
				((CAbilitySpellBase) existingAbility).populate(abilityEditorData, level);
			}
		};
	}

	public static interface AbilityConstructor {
		CAbilitySpellBase create(int handleId, War3ID alias);
	}
}
