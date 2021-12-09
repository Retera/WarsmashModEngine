package com.etheller.interpreter.ast.expression;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public class FunctionCallJassExpression implements JassExpression {
	private final String functionName;
	private final List<JassExpression> arguments;

	public FunctionCallJassExpression(final String functionName, final List<JassExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final JassFunction functionByName = globalScope.getFunctionByName(this.functionName);
		if (functionByName == null) {
			throw new JassException(globalScope, "Undefined function: " + this.functionName, new RuntimeException());
		}
		final List<JassValue> evaluatedExpressions = new ArrayList<>();
		for (final JassExpression expr : this.arguments) {
			final JassValue evaluatedExpression = expr.evaluate(globalScope, localScope, triggerScope);
			evaluatedExpressions.add(evaluatedExpression);
		}
		try {
			return functionByName.call(evaluatedExpressions, globalScope, triggerScope);
		}
		catch (final Exception exc) {
			throw new JassException(globalScope, "Function call by name failed for name: " + this.functionName, exc);
		}
	}

}
