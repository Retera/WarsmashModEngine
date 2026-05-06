package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.transformation;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionMergeUnits implements ABSingleAction {

	private ABUnitCallback unit1;
	private ABUnitCallback unit2;
	private ABIDCallback newUnitId;
	private ABLocationCallback location;
	private ABFloatCallback facing;

	private ABIntegerCallback playerIndex;
	private ABBooleanCallback resetHpMp;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		int thePlayerId = caster.getPlayerIndex();
		float theFacing = caster.getFacing();
		AbilityPointTarget loc = null;
		if (this.playerIndex != null) {
			thePlayerId = this.playerIndex.callback(caster, localStore, castId);
		}
		if (this.facing != null) {
			theFacing = this.facing.callback(caster, localStore, castId);
		}
		if (this.location != null) {
			loc = this.location.callback(caster, localStore, castId);
		} else {
			loc = new AbilityPointTarget(caster.getX(), caster.getY());
		}

		final CUnit u1 = this.unit1.callback(caster, localStore, castId);
		final CUnit u2 = this.unit2.callback(caster, localStore, castId);

		final float newHPPcnt = ((u1.getLife() / u1.getMaximumLife()) + (u2.getLife() / u2.getMaximumLife())) / 2;
		final float newMPPcnt = ((u1.getMana() / u1.getMaximumMana()) + (u2.getMana() / u2.getMaximumMana())) / 2;

		final CUnit createdUnit = localStore.game.createUnit(this.newUnitId.callback(caster, localStore, castId),
				thePlayerId, loc.getX(), loc.getY(), theFacing);
		if ((this.resetHpMp == null) || !this.resetHpMp.callback(caster, localStore, castId)) {
			createdUnit.setLife(localStore.game, newHPPcnt * createdUnit.getMaximumLife());
			createdUnit.setMana(newMPPcnt * createdUnit.getMaximumMana());
		}
		localStore.game.unitPreferredSelectionReplacement(u1, createdUnit);
		localStore.game.unitPreferredSelectionReplacement(u2, createdUnit);

		localStore.game.removeUnit(u1);
		localStore.game.removeUnit(u2);

		localStore.put(ABLocalStoreKeys.LASTCREATEDUNIT, createdUnit);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String playerExpression;
		if (this.playerIndex != null) {
			playerExpression = "Player(" + this.playerIndex.generateJassEquivalent(jassTextGenerator) + ")";
		} else {
			playerExpression = "GetOwningPlayer(" + jassTextGenerator.getCaster() + ")";
		}
		String facingExpression;
		if (this.facing != null) {
			facingExpression = this.facing.generateJassEquivalent(jassTextGenerator);
		} else {
			facingExpression = "GetUnitFacing(" + jassTextGenerator.getCaster() + ")";
		}
		String locExpression;
		if (this.location != null) {
			locExpression = this.location.generateJassEquivalent(jassTextGenerator);
		} else {
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
