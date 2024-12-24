package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCreateUnit implements ABSingleAction {

	private ABIDCallback id;
	private ABIntegerCallback playerIndex;
	private ABLocationCallback loc;
	private ABFloatCallback facing;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		int thePlayerId = caster.getPlayerIndex();
		float theFacing = 0;
		if (this.playerIndex != null) {
			thePlayerId = this.playerIndex.callback(game, caster, localStore, castId);
		}
		if (this.facing != null) {
			theFacing = this.facing.callback(game, caster, localStore, castId);
		}
		final AbilityPointTarget location = this.loc.callback(game, caster, localStore, castId);
		final CUnit createdUnit = game.createUnitSimple(this.id.callback(game, caster, localStore, castId), thePlayerId,
				location.getX(), location.getY(), theFacing);

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String playerIndexExpression;
		if (this.playerIndex != null) {
			playerIndexExpression = this.playerIndex.generateJassEquivalent(jassTextGenerator);
		}
		else {
			playerIndexExpression = "GetOwningPlayer(" + jassTextGenerator.getCaster() + ")";
		}

		String facingExpression = "bj_UNIT_FACING";
		if (this.facing != null) {
			facingExpression = this.facing.generateJassEquivalent(jassTextGenerator);
		}

		return "CreateUnitAtLoc(" + playerIndexExpression + ", " + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", " + this.loc.generateJassEquivalent(jassTextGenerator) + ", " + facingExpression + ")";
	}

}
