package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeWispHarvest extends CAbilityType<CAbilityTypeWispHarvestLevelData> {

	public CAbilityTypeWispHarvest(final War3ID alias, final War3ID code,
			final List<CAbilityTypeWispHarvestLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeWispHarvestLevelData levelData = getLevelData(0);
		return new CAbilityWispHarvest(handleId, getAlias(), levelData.getLumberPerInterval(),
				levelData.getArtAttachmentHeight(), levelData.getCastRange(), levelData.getDuration());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeWispHarvestLevelData levelData = getLevelData(level - 1);
		final CAbilityWispHarvest heroAbility = ((CAbilityWispHarvest) existingAbility);

		heroAbility.setLumberPerInterval(levelData.getLumberPerInterval());
		heroAbility.setArtAttachmentHeight(levelData.getArtAttachmentHeight());
		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setPeriodicIntervalLength(levelData.getDuration());

		heroAbility.setLevel(level);
	}
}
