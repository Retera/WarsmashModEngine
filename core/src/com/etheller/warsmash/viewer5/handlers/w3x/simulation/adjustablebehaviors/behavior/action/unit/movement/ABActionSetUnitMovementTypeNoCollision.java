package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.movement;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionSetUnitMovementTypeNoCollision implements ABSingleAction {

	private ABUnitCallback unit;
	private ABBooleanCallback active;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final CUnit targetUnit = this.unit.callback(caster, localStore, castId);
		if (this.active != null) {
			targetUnit.setNoCollisionMovementType(this.active.callback(caster, localStore, castId));
		} else {
			targetUnit.setNoCollisionMovementType(true);
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String activeExpression = "true";
		if (this.active != null) {
			activeExpression = this.active.generateJassEquivalent(jassTextGenerator);
		}
		return "SetUnitMovementTypeNoCollision(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", "
				+ activeExpression + ")";
	}

}
