package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
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

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeCoupleInstantLevelData levelData = getLevelData(level - 1);
		final CAbilityCoupleInstant heroAbility = ((CAbilityCoupleInstant) existingAbility);

		heroAbility.setResultingUnitType(levelData.getResultingUnitTypeId());
		heroAbility.setPartnerUnitType(levelData.getPartnerUnitTypeId());
		heroAbility.setMoveToPartner(levelData.isMoveToPartner());
		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setArea(levelData.getArea());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());
		heroAbility.setGoldCost(levelData.getGoldCost());
		heroAbility.setLumberCost(levelData.getLumberCost());

		heroAbility.setLevel(level);
	}

}
