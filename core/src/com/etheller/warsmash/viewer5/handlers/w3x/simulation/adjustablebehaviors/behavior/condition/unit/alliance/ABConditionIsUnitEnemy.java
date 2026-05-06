package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.unit.alliance;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;

public class ABConditionIsUnitEnemy extends ABBooleanCallback {

	private ABUnitCallback self;
	private ABUnitCallback unit;

	@Override
	public Boolean callback(CUnit casterUnit, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = unit.callback(casterUnit, localStore, castId);
		CUnit theCaster = casterUnit;
		if (this.self != null) {
			theCaster = this.self.callback(casterUnit, localStore, castId);
		}

		if (theUnit != null) {
			return !localStore.game.getPlayer(theUnit.getPlayerIndex()).hasAlliance(theCaster.getPlayerIndex(),
					CAllianceType.PASSIVE);
		}
		return false;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String casterExpr;
		if (this.self == null) {
			casterExpr = jassTextGenerator.getCaster();
		} else {
			casterExpr = this.self.generateJassEquivalent(jassTextGenerator);
		}
		return "IsUnitEnemy(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", GetOwningPlayer(" + casterExpr
				+ "))";
	}

}
