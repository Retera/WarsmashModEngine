package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABCallbackGetCastingPlayer extends ABPlayerCallback {
	
	@Override
	public CPlayer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return game.getPlayer(caster.getPlayerIndex());
	}

}
