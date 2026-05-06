package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.unit.state;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionIsUnitVisible extends ABBooleanCallback {

	private ABUnitCallback caster;
	private ABUnitCallback unit;

	@Override
	public Boolean callback(CUnit casterUnit, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = unit.callback(casterUnit, localStore, castId);
		CUnit theCaster = casterUnit;
		if (this.caster != null) {
			theCaster = this.caster.callback(casterUnit, localStore, castId);
		}

		if (theUnit != null) {
			return theUnit.isVisible(localStore.game, theCaster.getPlayerIndex());
		}
		return false;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String casterExpr;
		if (this.caster == null) {
			casterExpr = jassTextGenerator.getCaster();
		} else {
			casterExpr = this.caster.generateJassEquivalent(jassTextGenerator);
		}
		return "IsUnitEnemy(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", GetOwningPlayer(" + casterExpr
				+ "))";
	}

}
