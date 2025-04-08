package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unit;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABConditionSuccessfullyChargeResources extends ABCondition {

	private ABUnitCallback unit;
	private ABPlayerCallback player;

	private ABIntegerCallback mana;
	private ABIntegerCallback gold;
	private ABIntegerCallback lumber;
	private ABIntegerCallback food;

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(game, caster, localStore, castId);
		}
		CPlayer thePlayer = game.getPlayer(theUnit.getPlayerIndex());
		if (player != null) {
			thePlayer = player.callback(game, caster, localStore, castId);
		}

		int manaCost = 0;
		int goldCost = 0;
		int lumberCost = 0;
		int foodCost = 0;
		if (mana != null) {
			manaCost = mana.callback(game, caster, localStore, castId);
		}
		if (gold != null) {
			goldCost = gold.callback(game, caster, localStore, castId);
		}
		if (lumber != null) {
			lumberCost = lumber.callback(game, caster, localStore, castId);
		}
		if (food != null) {
			foodCost = food.callback(game, caster, localStore, castId);
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
