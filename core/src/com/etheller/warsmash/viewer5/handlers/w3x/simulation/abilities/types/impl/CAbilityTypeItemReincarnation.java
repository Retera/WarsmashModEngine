package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemReincarnation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;

import java.util.List;

public class CAbilityTypeItemReincarnation extends CAbilityType<CAbilityTypeItemReincarnationLevelData> {

	public CAbilityTypeItemReincarnation(final War3ID alias, final War3ID code,
                                         final List<CAbilityTypeItemReincarnationLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeItemReincarnationLevelData levelData = getLevelData(0);
		return new CAbilityItemReincarnation(handleId, getAlias(), levelData.getDelay(), levelData.getRestoredLife(),
				levelData.getRestoredMana());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeLevelData levelData = getLevelData(level - 1);
		final CLevelingAbility heroAbility = (existingAbility);

		// TODO ignores fields

		heroAbility.setLevel(level);
	}
}
