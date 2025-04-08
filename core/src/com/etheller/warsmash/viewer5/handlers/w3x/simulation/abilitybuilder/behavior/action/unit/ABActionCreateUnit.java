package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionCreateUnit implements ABSingleAction {

	private ABIDCallback id;
	private ABPlayerCallback owner;
	private ABLocationCallback loc;
	private ABFloatCallback facing;
	
	private ABBooleanCallback addSummonedTag;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CPlayer thePlayer = null;
		float theFacing = 0;
		if (this.owner != null) {
			thePlayer = this.owner.callback(game, caster, localStore, castId);
		} else {
			thePlayer = game.getPlayer(caster.getPlayerIndex());
		}
		if (this.facing != null) {
			theFacing = this.facing.callback(game, caster, localStore, castId);
		}
		final AbilityPointTarget location = this.loc.callback(game, caster, localStore, castId);
		final CUnit createdUnit = game.createUnitSimple(this.id.callback(game, caster, localStore, castId), thePlayer.getId(),
				location.getX(), location.getY(), theFacing);
		
		if (addSummonedTag == null || addSummonedTag.callback(game, caster, localStore, castId)) {
			createdUnit.addClassification(CUnitClassification.SUMMONED);
		}

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String playerIndexExpression;
		if (this.owner != null) {
			playerIndexExpression = this.owner.generateJassEquivalent(jassTextGenerator);
		}
		else {
			playerIndexExpression = "TODOJASS(" + jassTextGenerator.getCaster() + ")";
		}

		String facingExpression = "bj_UNIT_FACING";
		if (this.facing != null) {
			facingExpression = this.facing.generateJassEquivalent(jassTextGenerator);
		}

		return "CreateUnitAtLoc(" + playerIndexExpression + ", " + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", " + this.loc.generateJassEquivalent(jassTextGenerator) + ", " + facingExpression + ")";
	}

}
