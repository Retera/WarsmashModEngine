package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionAddDefenseBonus implements ABSingleAction {

	private ABUnitCallback targetUnit;
	private ABFloatCallback defenseValue;
	private ABBooleanCallback percentage;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CUnit target = this.targetUnit.callback(game, caster, localStore, castId);

		if (this.percentage.callback(game, caster, localStore, castId)) {
			// TODO need to fix percents
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() + this.defenseValue.callback(game, caster, localStore, castId));
		}
		else {
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() + this.defenseValue.callback(game, caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "UnitAddDefenseBonus(" + this.targetUnit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.defenseValue.generateJassEquivalent(jassTextGenerator) + ")//, "
				+ this.percentage.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
