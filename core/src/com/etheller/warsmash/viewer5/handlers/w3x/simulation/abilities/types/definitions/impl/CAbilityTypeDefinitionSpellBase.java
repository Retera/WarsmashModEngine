package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;

public class CAbilityTypeDefinitionSpellBase implements CAbilityTypeDefinition {
	private final AbilityConstructor abilityConstructor;

	public CAbilityTypeDefinitionSpellBase(final AbilityConstructor abilityConstructor) {
		this.abilityConstructor = abilityConstructor;
	}

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final GameObject abilityEditorData) {
		final List<CAbilityTypeLevelData> emptyLevelDatas = new ArrayList<>();
		final int levels = abilityEditorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
		for (int i = 0; i < levels; i++) {
			// NOTE: for now, size of this list is getting used for ability's max level for
			// heroes
			emptyLevelDatas.add(null);
		}
		return new CAbilityType<CAbilityTypeLevelData>(alias,
				abilityEditorData.getFieldAsWar3ID(AbilityFields.CODE, -1), emptyLevelDatas) {
			@Override
			public CAbility createAbility(final int handleId) {
				final CAbilitySpell spellAbility = CAbilityTypeDefinitionSpellBase.this.abilityConstructor
						.create(handleId, getAlias());
				spellAbility.populate(abilityEditorData, 1);
				return spellAbility;
			}

			@Override
			public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
				existingAbility.setLevel(game, unit, level);
				((CAbilitySpell) existingAbility).populate(abilityEditorData, level);
			}
		};
	}

	public static interface AbilityConstructor {
		CAbilitySpell create(int handleId, War3ID alias);
	}
}
