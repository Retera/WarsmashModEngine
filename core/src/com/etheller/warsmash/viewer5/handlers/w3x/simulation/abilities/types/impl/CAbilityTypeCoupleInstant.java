package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityCoupleInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeCoupleInstant extends CAbilityType<CAbilityTypeCoupleInstantLevelData> {

	public CAbilityTypeCoupleInstant(final War3ID alias, final War3ID code,
			final List<CAbilityTypeCoupleInstantLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeCoupleInstantLevelData levelData = getLevelData(0);
		return new CAbilityCoupleInstant(handleId, getAlias(), levelData.getResultingUnitTypeId(),
				levelData.getPartnerUnitTypeId(), levelData.isMoveToPartner(), levelData.getCastRange(),
				levelData.getArea(), levelData.getTargetsAllowed(), levelData.getGoldCost(), levelData.getLumberCost());
	}

}
