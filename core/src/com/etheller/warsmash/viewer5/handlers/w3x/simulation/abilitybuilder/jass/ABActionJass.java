package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionJass implements ABAction {
	private final CodeJassValue jassFunction;

	public ABActionJass(final CodeJassValue jassFunction) {
		this.jassFunction = jassFunction;
	}

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final GlobalScope globalScope = game.getGlobalScope();
		final JassThread thread = globalScope.createThread(this.jassFunction,
				CommonTriggerExecutionScope.abilityBuilder(caster, localStore, castId));
		globalScope.runThreadUntilCompletion(thread);
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		throw new UnsupportedOperationException();
	}

	public static List<ABAction> wrap(final CodeJassValue jassFunction) {
		if (jassFunction == null) {
			return Collections.emptyList();
		}
		else {
			return Arrays.asList(new ABActionJass(jassFunction));
		}
	}

}
