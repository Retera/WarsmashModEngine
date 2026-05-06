package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.stats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionSetMp implements ABSingleAction {

	private ABUnitCallback target;
	private ABFloatCallback amount;
	private ABBooleanCallback isPercent;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		boolean percent = false;
		if (this.isPercent != null) {
			percent = this.isPercent.callback(caster, localStore, castId);
		}
		final CUnit targetUnit = this.target.callback(caster, localStore, castId);
		if (percent) {
			targetUnit.setMana(Math
					.max(Math.min(this.amount.callback(caster, localStore, castId) * targetUnit.getMaximumMana(),
							targetUnit.getMaximumMana()), 0));
		} else {
			targetUnit.setMana(Math.max(
					Math.min(this.amount.callback(caster, localStore, castId), targetUnit.getMaximumMana()), 0));
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String percentExpression = "false";
		if (this.isPercent != null) {
			percentExpression = this.isPercent.generateJassEquivalent(jassTextGenerator);
		}
		return "SetUnitMpAU(" + this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.amount.generateJassEquivalent(jassTextGenerator) + ", " + percentExpression + ")";
	}

}
