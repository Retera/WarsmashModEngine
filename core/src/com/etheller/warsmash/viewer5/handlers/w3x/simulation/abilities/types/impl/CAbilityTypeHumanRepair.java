package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

import java.util.List;

public class CAbilityTypeHumanRepair extends CAbilityType<CAbilityTypeHumanRepairLevelData> {

	public CAbilityTypeHumanRepair(final War3ID alias, final War3ID code,
								   final List<CAbilityTypeHumanRepairLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeHumanRepairLevelData levelData = getLevelData(0);
		//System.out.println("Time: "+levelData.getPowerbuildTimeRatio() + " Cost: " + levelData.getPowerbuildCostRatio() );
		return new CAbilityHumanRepair(handleId, getAlias(), levelData.getTargetsAllowed(),
				levelData.getNavalRangeBonus(), levelData.getRepairCostRatio(), levelData.getRepairTimeRatio(),
				levelData.getCastRange(), levelData.getPowerbuildCostRatio(), levelData.getPowerbuildTimeRatio());
	}

}
