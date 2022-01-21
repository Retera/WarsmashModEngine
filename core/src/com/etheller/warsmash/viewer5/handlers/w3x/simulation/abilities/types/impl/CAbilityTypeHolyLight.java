package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin.CAbilityHolyLight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeHolyLight extends CAbilityType<CAbilityTypeHolyLightLevelData> {

	public CAbilityTypeHolyLight(final War3ID alias, final War3ID code,
			final List<CAbilityTypeHolyLightLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeHolyLightLevelData levelData = getLevelData(0);
		return new CAbilityHolyLight(handleId, getAlias(), levelData.getManaCost(), levelData.getHealAmount(),
				levelData.getCastRange(), levelData.getCooldown(), levelData.getTargetsAllowed());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {

		final CAbilityTypeHolyLightLevelData levelData = getLevelData(level - 1);
		final CAbilityHolyLight heroAbility = ((CAbilityHolyLight) existingAbility);

		heroAbility.setManaCost(levelData.getManaCost());
		heroAbility.setHealAmount(levelData.getHealAmount());
		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setCooldown(levelData.getCooldown());
		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());

		heroAbility.setLevel(level);

	}

}
