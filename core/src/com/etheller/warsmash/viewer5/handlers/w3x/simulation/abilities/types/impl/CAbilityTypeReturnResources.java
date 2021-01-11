package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityTypeReturnResources extends CAbilityType<CAbilityTypeReturnResourcesLevelData> {

	public CAbilityTypeReturnResources(final War3ID alias, final War3ID code,
			final List<CAbilityTypeReturnResourcesLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeReturnResourcesLevelData levelData = getLevelData(0);
		final EnumSet<ResourceType> acceptedResourceTypes = EnumSet.noneOf(ResourceType.class);
		if (levelData.isAcceptsGold()) {
			acceptedResourceTypes.add(ResourceType.GOLD);
		}
		if (levelData.isAcceptsLumber()) {
			acceptedResourceTypes.add(ResourceType.LUMBER);
		}
		return new CAbilityReturnResources(handleId, getAlias(), acceptedResourceTypes);
	}

}
