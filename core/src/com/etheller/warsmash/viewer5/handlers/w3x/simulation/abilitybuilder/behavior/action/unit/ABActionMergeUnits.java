package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionMergeUnits implements ABAction {

	private ABUnitCallback unit1;
	private ABUnitCallback unit2;
	private ABIDCallback newUnitId;
	private ABLocationCallback location;
	private ABFloatCallback facing;

	private ABIntegerCallback playerIndex;
	private ABBooleanCallback resetHpMp;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		int thePlayerId = caster.getPlayerIndex();
		float theFacing = caster.getFacing();
		AbilityPointTarget loc = null;
		if (playerIndex != null) {
			thePlayerId = playerIndex.callback(game, caster, localStore, castId);
		}
		if (facing != null) {
			theFacing = facing.callback(game, caster, localStore, castId);
		}
		if (location != null) {
			loc = location.callback(game, caster, localStore, castId);
		} else {
			loc = new AbilityPointTarget(caster.getX(), caster.getY());
		}
		
		CUnit u1 = unit1.callback(game, caster, localStore, castId);
		CUnit u2 = unit2.callback(game, caster, localStore, castId);
		
		float newHPPcnt = ((u1.getLife() / u1.getMaximumLife()) + (u2.getLife() / u2.getMaximumLife())) / 2;
		float newMPPcnt = ((u1.getMana() / u1.getMaximumMana()) + (u2.getMana() / u2.getMaximumMana())) / 2;
		
		
		
		CUnit createdUnit = game.createUnit(newUnitId.callback(game, caster, localStore, castId), thePlayerId,
				loc.getX(), loc.getY(), theFacing);
		if (resetHpMp == null || !resetHpMp.callback(game, caster, localStore, castId)) {
			createdUnit.setLife(game, newHPPcnt * createdUnit.getMaximumLife());
			createdUnit.setMana(newMPPcnt * createdUnit.getMaximumMana());
		}
		game.unitPreferredSelectionReplacement(u1, createdUnit);
		game.unitPreferredSelectionReplacement(u2, createdUnit);

		game.removeUnit(u1);
		game.removeUnit(u2);

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

}
