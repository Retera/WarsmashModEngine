package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass;

import java.util.Collections;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class JassFunctionBehaviorExpr implements BehaviorExpr {
	private final JassFunction function;

	public JassFunctionBehaviorExpr(final JassFunction function) {
		this.function = function;
	}

	@Override
	public CBehavior evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		return this.function.call(Collections.emptyList(), globalScope, triggerScope)
				.visit(ObjectJassValueVisitor.getInstance());
	}

}
