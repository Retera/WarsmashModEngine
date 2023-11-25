package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABCallbackGetOwnerOfUnit extends ABPlayerCallback {
	
	private ABUnitCallback unit;
	
	@Override
	public CPlayer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return game.getPlayer(unit.callback(game, caster, localStore, castId).getPlayerIndex());
	}

}
