package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABActionJass implements ABAction {
	private JassFunction jassFunction;

	public ABActionJass(JassFunction jassFunction) {
		this.jassFunction = jassFunction;
	}

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		this.jassFunction.call(Collections.emptyList(), game.getGlobalScope(),
				CommonTriggerExecutionScope.abilityBuilder(caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		throw new UnsupportedOperationException();
	}

	public static List<ABAction> wrap(JassFunction jassFunction) {
		if (jassFunction == null) {
			return Collections.emptyList();
		}
		else {
			return Arrays.asList(new ABActionJass(jassFunction));
		}
	}
}
