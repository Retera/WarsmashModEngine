package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABActionCreateUnit implements ABSingleAction {

	private ABIDCallback id;
	private ABPlayerCallback owner;
	private ABLocationCallback loc;
	private ABFloatCallback facing;

	private ABBooleanCallback addSummonedTag;
	private ABBooleanCallback removeFood;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		War3ID theId = id.callback(caster, localStore, castId);
		if (theId == null) {
			return;
		}

		CPlayer thePlayer = null;
		float theFacing = 0;
		if (this.owner != null) {
			thePlayer = this.owner.callback(caster, localStore, castId);
		} else {
			thePlayer = localStore.game.getPlayer(caster.getPlayerIndex());
		}
		if (this.facing != null) {
			theFacing = this.facing.callback(caster, localStore, castId);
		}
		final AbilityPointTarget location = this.loc.callback(caster, localStore, castId);
		final CUnit createdUnit = localStore.game.createUnitSimple(theId, thePlayer.getId(), location.getX(),
				location.getY(), theFacing);

		if (addSummonedTag == null || addSummonedTag.callback(caster, localStore, castId)) {
			createdUnit.addClassification(CUnitClassification.SUMMONED);
		}

		if (removeFood != null && removeFood.callback(caster, localStore, castId)) {
			thePlayer.setUnitFoodUsed(createdUnit, 0);
			thePlayer.setUnitFoodMade(createdUnit, 0);
		}

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String playerIndexExpression;
		if (this.owner != null) {
			playerIndexExpression = this.owner.generateJassEquivalent(jassTextGenerator);
		} else {
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
