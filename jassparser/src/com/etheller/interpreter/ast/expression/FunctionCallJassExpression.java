package com.etheller.interpreter.ast.expression;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassValue;

public class FunctionCallJassExpression implements JassExpression {
	private final String functionName;
	private final List<JassExpression> arguments;

	public FunctionCallJassExpression(final String functionName, final List<JassExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope) {
		final JassFunction functionByName = globalScope.getFunctionByName(functionName);
		if (functionByName == null) {
			throw new RuntimeException("Undefined function: " + functionName);
		}
		final List<JassValue> evaluatedExpressions = new ArrayList<>();
		for (final JassExpression expr : arguments) {
			final JassValue evaluatedExpression = expr.evaluate(globalScope, localScope);
			evaluatedExpressions.add(evaluatedExpression);
		}
		return functionByName.call(evaluatedExpressions, globalScope);
	}

}
