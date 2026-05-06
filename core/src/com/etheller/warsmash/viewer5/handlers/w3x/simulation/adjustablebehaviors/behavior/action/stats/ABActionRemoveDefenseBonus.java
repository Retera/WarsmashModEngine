package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.stats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionRemoveDefenseBonus implements ABSingleAction {

	private ABUnitCallback targetUnit;
	private ABFloatCallback defenseValue;
	private ABBooleanCallback percentage;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final CUnit target = this.targetUnit.callback(caster, localStore, castId);

		if (this.percentage.callback(caster, localStore, castId)) {
			// TODO need to fix percents
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() - this.defenseValue.callback(caster, localStore, castId));
		} else {
			target.setTemporaryDefenseBonus(
					target.getTemporaryDefenseBonus() - this.defenseValue.callback(caster, localStore, castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "UnitAddDefenseBonus(" + this.targetUnit.generateJassEquivalent(jassTextGenerator) + ", -("
				+ this.defenseValue.generateJassEquivalent(jassTextGenerator) + "))//, "
				+ this.percentage.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
