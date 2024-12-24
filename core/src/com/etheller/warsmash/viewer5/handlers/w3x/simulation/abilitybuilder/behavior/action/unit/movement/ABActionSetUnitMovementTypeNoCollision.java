package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit.movement;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionSetUnitMovementTypeNoCollision implements ABSingleAction {

	private ABUnitCallback unit;
	private ABBooleanCallback active;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CUnit targetUnit = this.unit.callback(game, caster, localStore, castId);
		if (this.active != null) {
			targetUnit.setNoCollisionMovementType(this.active.callback(game, caster, localStore, castId));
		}
		else {
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
