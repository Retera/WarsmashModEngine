package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;

public class ABConditionIsUnitEnemy extends ABCondition {

	private ABUnitCallback self;
	private ABUnitCallback unit;

	@Override
	public Boolean callback(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		CUnit theUnit = unit.callback(game, casterUnit, localStore, castId);
		CUnit theCaster = casterUnit;
		if (this.self != null) {
			theCaster = this.self.callback(game, casterUnit, localStore, castId);
		}

		if (theUnit != null) {
			return !game.getPlayer(theUnit.getPlayerIndex()).hasAlliance(theCaster.getPlayerIndex(),
					CAllianceType.PASSIVE);
		}
		return false;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String casterExpr;
		if (this.self == null) {
			casterExpr = jassTextGenerator.getCaster();
		}
		else {
			casterExpr = this.self.generateJassEquivalent(jassTextGenerator);
		}
		return "IsUnitEnemy(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", GetOwningPlayer(" + casterExpr
				+ "))";
	}

}
