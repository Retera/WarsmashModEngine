package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.Aliased;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;

public interface AbilityBuilderAbility extends CLevelingAbility, Aliased {
	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData();

	public AbilityBuilderConfiguration getConfig();

	public Map<String, Object> getLocalStore();
	
	public float getArea();
	
	public float getCooldown();

	public float getCastRange();

	public void startCooldown(CSimulation game, CUnit unit);

	public void resetCooldown(CSimulation game, CUnit unit);

	public float getCooldownRemainingTicks(CSimulation game, CUnit unit);
	
	War3ID getOnTooltipOverride();
	
	public int getAbilityIntField(String field);

}
