package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.Aliased;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;

public interface ABAbilityBuilderAbility extends CLevelingAbility, Aliased {
	public List<ABAbilityBuilderAbilityTypeLevelData> getLevelData();

	public ABAbilityBuilderConfiguration getConfig();

	public ABLocalDataStore getLocalStore();
	
	public float getArea();
	
	public float getCooldown();

	public float getCastRange();

	public float getCastTime();

	public void startCooldown(CSimulation game, CUnit unit);

	public void resetCooldown(CSimulation game, CUnit unit);

	public float getCooldownRemainingTicks(CSimulation game, CUnit unit);
	
	War3ID getOnTooltipOverride();
	
	public int getAbilityIntField(String field);
	public float getAbilityFloatField(String field);
	public String getAbilityStringField(String field);
	public boolean getAbilityBooleanField(String field);

	boolean hasUniqueFlag(String flag);
	void addUniqueFlag(String flag);
	void removeUniqueFlag(String flag);

	public int getIconVisibleMenuId();
	void setIconVisibleMenuId(int menu);

}
