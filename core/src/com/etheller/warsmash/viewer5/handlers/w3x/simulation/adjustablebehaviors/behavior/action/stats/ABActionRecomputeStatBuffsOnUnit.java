
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.stats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABNonStackingStatBuffTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;

public class ABActionRecomputeStatBuffsOnUnit implements ABSingleAction {

	private ABUnitCallback targetUnit;
	private ABNonStackingStatBuffTypeCallback buffType;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final CUnit unit = this.targetUnit.callback(caster, localStore, castId);
		NonStackingStatBuffType btype = this.buffType.callback(caster, localStore, castId);
		if (btype.isHeroStat()) {
			unit.computeDerivedHeroFields(localStore.game, btype);
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