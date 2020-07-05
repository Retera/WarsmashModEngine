package com.etheller.warsmash.parsers.jass.triggers;

import java.util.Collections;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class BoolExprFilter implements TriggerBooleanExpression {
	private final JassFunction takesNothingReturnsBooleanFunction;

	public BoolExprFilter(final JassFunction returnsBooleanFunction) {
		this.takesNothingReturnsBooleanFunction = returnsBooleanFunction;
	}

	@Override
	public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		final JassValue booleanJassReturnValue = this.takesNothingReturnsBooleanFunction.call(Collections.EMPTY_LIST,
				globalScope, triggerScope);
		final Boolean booleanReturnValue = booleanJassReturnValue.visit(BooleanJassValueVisitor.getInstance());
		return booleanReturnValue.booleanValue();
	}

}
