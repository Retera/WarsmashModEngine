package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionCreateUnit implements ABAction {

	private ABIDCallback id;
	private ABIntegerCallback playerIndex;
	private ABLocationCallback loc;
	private ABFloatCallback facing;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		int thePlayerId = caster.getPlayerIndex();
		float theFacing = 0;
		if (playerIndex != null) {
			thePlayerId = playerIndex.callback(game, caster, localStore, castId);
		}
		if (facing != null) {
			theFacing = facing.callback(game, caster, localStore, castId);
		}
		AbilityPointTarget location = loc.callback(game, caster, localStore, castId);
		CUnit createdUnit = game.createUnitSimple(id.callback(game, caster, localStore, castId), thePlayerId,
				location.getX(), location.getY(), theFacing);

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

}
