package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

import java.util.List;

public class CAbilityTypeWispHarvest extends CAbilityType<CAbilityTypeWispHarvestLevelData> {

	public CAbilityTypeWispHarvest(final War3ID alias, final War3ID code,
                                   final List<CAbilityTypeWispHarvestLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeWispHarvestLevelData levelData = getLevelData(0);
		return new CAbilityWispHarvest(handleId, getAlias(), levelData.getLumberPerInterval(), levelData.getArtAttachmentHeight(),
				levelData.getCastRange(), levelData.getDuration());
	}

}
