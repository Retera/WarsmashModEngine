package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.blight.CAbilityBlight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeBlight extends CAbilityType<CAbilityTypeBlightLevelData> {

	public CAbilityTypeBlight(final War3ID alias, final War3ID code,
			final List<CAbilityTypeBlightLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeBlightLevelData levelData = getLevelData(0);
		return new CAbilityBlight(handleId, getAlias(), levelData.isCreatesBlight(), levelData.getExpansionAmount(),
				levelData.getAreaOfEffect(), levelData.getGameSecondsPerBlightExpansion());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeBlightLevelData levelData = getLevelData(level - 1);
		final CAbilityBlight heroAbility = ((CAbilityBlight) existingAbility);

		heroAbility.setCreatesBlight(levelData.isCreatesBlight());
		heroAbility.setAreaOfEffect(levelData.getAreaOfEffect());
		heroAbility.setExpansionAmount(levelData.getExpansionAmount());
		heroAbility.setGameSecondsPerBlightExpansion(levelData.getGameSecondsPerBlightExpansion());

		heroAbility.setLevel(level);
	}
}
