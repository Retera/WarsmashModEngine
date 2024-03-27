package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.player;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionGiveResourcesToPlayer implements ABAction {

	private ABPlayerCallback player;
	private ABIntegerCallback gold;
	private ABIntegerCallback lumber;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CPlayer p = player.callback(game, caster, localStore, castId);
		if (gold != null) {
			p.addGold(gold.callback(game, caster, localStore, castId));
		}
		if (lumber != null) {
			p.addLumber(lumber.callback(game, caster, localStore, castId));
		}
	}
}
