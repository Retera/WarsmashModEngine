package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.player;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionSetAbilityEnabledForPlayer implements ABSingleAction {

	private ABPlayerCallback player;
	private ABIDCallback abilityId;
	private ABBooleanCallback enabled;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final CPlayer p = this.player.callback(caster, localStore, castId);
		boolean e = true;
		if (this.enabled != null) {
			e = this.enabled.callback(caster, localStore, castId);
		}
		p.setAbilityEnabled(localStore.game, this.abilityId.callback(caster, localStore, castId), e);
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
