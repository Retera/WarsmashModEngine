
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.stats;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABNonStackingStatBuffTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class ABActionRecomputeStatBuffsOnUnit implements ABSingleAction {

	private ABUnitCallback targetUnit;
	private ABNonStackingStatBuffTypeCallback buffType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CUnit unit = this.targetUnit.callback(game, caster, localStore, castId);
		NonStackingStatBuffType btype = this.buffType.callback(game, caster, localStore, castId);
		if (btype.isHeroStat()) {
			unit.computeDerivedHeroFields(game, btype);
		} else {
			unit.computeDerivedFields(btype);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "RecomputeStatBuffsOnUnit(" + this.targetUnit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.buffType.generateJassEquivalent(jassTextGenerator) + ")";
	}
}