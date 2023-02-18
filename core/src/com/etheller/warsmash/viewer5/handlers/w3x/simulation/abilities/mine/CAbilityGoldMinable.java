package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;

public interface CAbilityGoldMinable {

	int getActiveMinerCount();

	int getMiningCapacity();

	void addMiner(CBehaviorHarvest cBehaviorHarvest);

	float getMiningDuration();

	int getGold();

	void setGold(int amount);

	boolean isBaseMine();
}
