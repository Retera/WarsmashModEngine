package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.player;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionSetAbilityEnabledForPlayer implements ABAction {

	private ABPlayerCallback player;
	private ABIDCallback abilityId;
	private ABBooleanCallback enabled;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CPlayer p = player.callback(game, caster, localStore, castId);
		boolean e = true;
		if (enabled != null) {
			e = enabled.callback(game, caster, localStore, castId);
		}
		p.setAbilityEnabled(game, abilityId.callback(game, caster, localStore, castId), e);
	}
}
