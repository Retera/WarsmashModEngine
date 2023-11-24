package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABCallbackGetPlayerById extends ABPlayerCallback {
	
	private ABIntegerCallback id;
	
	@Override
	public CPlayer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return game.getPlayer(id.callback(game, caster, localStore, castId));
	}

}
