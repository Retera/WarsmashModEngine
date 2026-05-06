package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionJass extends ABBooleanCallback {
	private final TriggerBooleanExpression boolExpr;

	public ABConditionJass(final TriggerBooleanExpression boolExpr) {
		this.boolExpr = boolExpr;
	}

	@Override
	public Boolean callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		return this.boolExpr.evaluate(localStore.game.getGlobalScope(),
				CommonTriggerExecutionScope.abilityBuilder(caster, localStore, castId));
	}

	public static List<ABBooleanCallback> wrap(final TriggerBooleanExpression jassFunction) {
		if (jassFunction == null) {
			return Collections.emptyList();
		} else {
			return Arrays.asList(new ABConditionJass(jassFunction));
		}
	}

}
