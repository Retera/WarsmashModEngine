package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage.CAbilityBlizzard;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeBlizzard extends CAbilityType<CAbilityTypeBlizzardLevelData> {

	public CAbilityTypeBlizzard(War3ID alias, War3ID code, int heroMaximumLevel,
			List<CAbilityTypeBlizzardLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeBlizzardLevelData levelData = getLevelData(0);
//		return new CAbilityBlizzard(handleId, getAlias(), levelData.getManaCost(), levelData.getCastRange(), levelData.getCooldown(), levelData.getCastingTime(), levelData.getTargetsAllowed(), null, null, levelData.getBu, handleId, handleId, handleId, handleId, handleId, handleId, handleId, getAlias())
		return null;
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeBlizzardLevelData levelData = getLevelData(level - 1);
		final CAbilityBlizzard heroAbility = (CAbilityBlizzard) existingAbility;

//		heroAbility.setManaCost(levelData.getManaCost());
//		heroAbility.setDamage(levelData.getDamage());
//		heroAbility.setCastRange(levelData.getCastRange());
//		heroAbility.setCooldown(levelData.getCooldown());
//		heroAbility.setDuration(levelData.getDuration());
//		heroAbility.setHeroDuration(levelData.getHeroDuration());
//		heroAbility.setBuffId(levelData.getBuffId());
//		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());

		heroAbility.setLevel(game, unit, level);

	}

}
