package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;

public class ABConditionIsUnitEnemy implements ABCondition {

	private ABUnitCallback caster;
	private ABUnitCallback unit;

	@Override
	public boolean evaluate(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		final CUnit theUnit = this.unit.callback(game, casterUnit, localStore, castId);
		CUnit theCaster = casterUnit;
		if (this.caster != null) {
			theCaster = this.caster.callback(game, casterUnit, localStore, castId);
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
		if (this.caster == null) {
			casterExpr = jassTextGenerator.getCaster();
		}
		else {
			casterExpr = this.caster.generateJassEquivalent(jassTextGenerator);
		}
		return "IsUnitEnemy(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", GetOwningPlayer(" + casterExpr
				+ "))";
	}

}
