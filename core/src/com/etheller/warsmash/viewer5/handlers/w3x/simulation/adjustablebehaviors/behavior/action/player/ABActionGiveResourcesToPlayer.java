package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.player;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionGiveResourcesToPlayer implements ABSingleAction {

	private ABPlayerCallback player;
	private ABIntegerCallback gold;
	private ABIntegerCallback lumber;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final CPlayer p = this.player.callback(caster, localStore, castId);
		if (this.gold != null) {
			p.addGold(this.gold.callback(caster, localStore, castId));
		}
		if (this.lumber != null) {
			p.addLumber(this.lumber.callback(caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String goldExpression = "0";
		if (this.gold != null) {
			goldExpression = this.gold.generateJassEquivalent(jassTextGenerator);
		}
		String lumberExpression = "0";
		if (this.lumber != null) {
			lumberExpression = this.lumber.generateJassEquivalent(jassTextGenerator);
		}

		return "GiveResourcesToPlayerAU(" + this.player.generateJassEquivalent(jassTextGenerator) + ", "
				+ goldExpression + ", " + lumberExpression + ")";
	}
}
