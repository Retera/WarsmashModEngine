package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition;

public class CAbilityTypeJass extends CAbilityType<CAbilityTypeLevelData> {

	private final CAbilityTypeJassDefinition abilityTypeJassDefinition;

	public CAbilityTypeJass(final War3ID alias, final War3ID code, final List<CAbilityTypeLevelData> levelData,
			final GlobalScope jassGlobalScope, final CAbilityTypeJassDefinition abilityTypeJassDefinition) {
		super(alias, code, levelData);
		this.abilityTypeJassDefinition = abilityTypeJassDefinition;
	}

	@Override
	public CAbility createAbility(final int handleId) {
		return new CAbilityJass(handleId, getCode(), getAlias(), this.abilityTypeJassDefinition);
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		existingAbility.setLevel(level);
	}
}
