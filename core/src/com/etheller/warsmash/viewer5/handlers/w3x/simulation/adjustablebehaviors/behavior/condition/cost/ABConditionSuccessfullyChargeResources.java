package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.cost;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABConditionSuccessfullyChargeResources extends ABBooleanCallback {

	private ABUnitCallback unit;
	private ABPlayerCallback player;

	private ABIntegerCallback mana;
	private ABIntegerCallback gold;
	private ABIntegerCallback lumber;
	private ABIntegerCallback food;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(caster, localStore, castId);
		}
		CPlayer thePlayer = localStore.game.getPlayer(theUnit.getPlayerIndex());
		if (player != null) {
			thePlayer = player.callback(caster, localStore, castId);
		}

		int manaCost = 0;
		int goldCost = 0;
		int lumberCost = 0;
		int foodCost = 0;
		if (mana != null) {
			manaCost = mana.callback(caster, localStore, castId);
		}
		if (gold != null) {
			goldCost = gold.callback(caster, localStore, castId);
		}
		if (lumber != null) {
			lumberCost = lumber.callback(caster, localStore, castId);
		}
		if (food != null) {
			foodCost = food.callback(caster, localStore, castId);
		}

		if ((manaCost > 0 && manaCost > theUnit.getMana()) || (goldCost > 0 && goldCost > thePlayer.getGold())
				|| (lumberCost > 0 && lumberCost > thePlayer.getLumber())
				|| (foodCost > 0 && foodCost > (thePlayer.getFoodCap() - thePlayer.getFoodUsed()))) {
			return false;
		}

		if (manaCost > 0)
			theUnit.chargeMana(manaCost);
		theUnit.notifyAbilitiesChanged();
		if (goldCost > 0 || lumberCost > 0)
			thePlayer.charge(goldCost, lumberCost);
		if (foodCost > 0)
			thePlayer.setFoodUsed(thePlayer.getFoodUsed() + foodCost);
		return true;
	}
}
