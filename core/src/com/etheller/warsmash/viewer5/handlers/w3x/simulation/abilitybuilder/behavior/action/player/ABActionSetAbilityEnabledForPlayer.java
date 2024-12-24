package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.player;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionSetAbilityEnabledForPlayer implements ABSingleAction {

	private ABPlayerCallback player;
	private ABIDCallback abilityId;
	private ABBooleanCallback enabled;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CPlayer p = this.player.callback(game, caster, localStore, castId);
		boolean e = true;
		if (this.enabled != null) {
			e = this.enabled.callback(game, caster, localStore, castId);
		}
		p.setAbilityEnabled(game, this.abilityId.callback(game, caster, localStore, castId), e);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String enabledExpression = "true";
		if (this.enabled != null) {
			enabledExpression = this.enabled.generateJassEquivalent(jassTextGenerator);
		}
		return "SetPlayerAbilityAvailable(" + this.player.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.abilityId.generateJassEquivalent(jassTextGenerator) + ", " + enabledExpression + ")";
	}
}
