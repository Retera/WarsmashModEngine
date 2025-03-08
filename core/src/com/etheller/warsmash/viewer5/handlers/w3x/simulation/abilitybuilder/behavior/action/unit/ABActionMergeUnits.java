package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionMergeUnits implements ABSingleAction {

	private ABUnitCallback unit1;
	private ABUnitCallback unit2;
	private ABIDCallback newUnitId;
	private ABLocationCallback location;
	private ABFloatCallback facing;

	private ABIntegerCallback playerIndex;
	private ABBooleanCallback resetHpMp;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		int thePlayerId = caster.getPlayerIndex();
		float theFacing = caster.getFacing();
		AbilityPointTarget loc = null;
		if (this.playerIndex != null) {
			thePlayerId = this.playerIndex.callback(game, caster, localStore, castId);
		}
		if (this.facing != null) {
			theFacing = this.facing.callback(game, caster, localStore, castId);
		}
		if (this.location != null) {
			loc = this.location.callback(game, caster, localStore, castId);
		}
		else {
			loc = new AbilityPointTarget(caster.getX(), caster.getY());
		}

		final CUnit u1 = this.unit1.callback(game, caster, localStore, castId);
		final CUnit u2 = this.unit2.callback(game, caster, localStore, castId);

		final float newHPPcnt = ((u1.getLife() / u1.getMaximumLife()) + (u2.getLife() / u2.getMaximumLife())) / 2;
		final float newMPPcnt = ((u1.getMana() / u1.getMaximumMana()) + (u2.getMana() / u2.getMaximumMana())) / 2;

		final CUnit createdUnit = game.createUnit(this.newUnitId.callback(game, caster, localStore, castId),
				thePlayerId, loc.getX(), loc.getY(), theFacing);
		if ((this.resetHpMp == null) || !this.resetHpMp.callback(game, caster, localStore, castId)) {
			createdUnit.setLife(game, newHPPcnt * createdUnit.getMaximumLife());
			createdUnit.setMana(newMPPcnt * createdUnit.getMaximumMana());
		}
		game.unitPreferredSelectionReplacement(u1, createdUnit);
		game.unitPreferredSelectionReplacement(u2, createdUnit);

		game.removeUnit(u1);
		game.removeUnit(u2);

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String playerExpression;
		if (this.playerIndex != null) {
			playerExpression = "Player(" + this.playerIndex.generateJassEquivalent(jassTextGenerator) + ")";
		}
		else {
			playerExpression = "GetOwningPlayer(" + jassTextGenerator.getCaster() + ")";
		}
		String facingExpression;
		if (this.facing != null) {
			facingExpression = this.facing.generateJassEquivalent(jassTextGenerator);
		}
		else {
			facingExpression = "GetUnitFacing(" + jassTextGenerator.getCaster() + ")";
		}
		String locExpression;
		if (this.location != null) {
			locExpression = this.location.generateJassEquivalent(jassTextGenerator);
		}
		else {
			locExpression = "GetUnitLoc(" + jassTextGenerator.getCaster() + ")";
		}

		String resetHpMpExpression = "false";
		if (this.resetHpMp != null) {
			resetHpMpExpression = this.resetHpMp.generateJassEquivalent(jassTextGenerator);
		}

		return "MergeUnitsAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.unit1.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.unit2.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.newUnitId.generateJassEquivalent(jassTextGenerator) + ", " + locExpression + ", "
				+ facingExpression + ", " + playerExpression + ", " + resetHpMpExpression + ")";
	}

}
