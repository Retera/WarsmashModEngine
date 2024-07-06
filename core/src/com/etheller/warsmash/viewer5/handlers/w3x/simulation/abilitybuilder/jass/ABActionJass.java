package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionJass implements ABAction {
	private final JassFunction jassFunction;

	public ABActionJass(final JassFunction jassFunction) {
		this.jassFunction = jassFunction;
	}

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		this.jassFunction.call(Collections.emptyList(), game.getGlobalScope(),
				CommonTriggerExecutionScope.abilityBuilder(caster, localStore, castId));
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		throw new UnsupportedOperationException();
	}

	public static List<ABAction> wrap(final JassFunction jassFunction) {
		if (jassFunction == null) {
			return Collections.emptyList();
		}
		else {
			return Arrays.asList(new ABActionJass(jassFunction));
		}
	}

}
