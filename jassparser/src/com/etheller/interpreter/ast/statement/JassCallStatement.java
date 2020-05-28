package com.etheller.interpreter.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassValue;

public class JassCallStatement implements JassStatement {
	private final String functionName;
	private final List<JassExpression> arguments;

	public JassCallStatement(final String functionName, final List<JassExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope) {
		final JassFunction functionByName = globalScope.getFunctionByName(functionName);
		if (functionByName == null) {
			throw new RuntimeException("Undefined function: " + functionName);
		}
		final List<JassValue> evaluatedExpressions = new ArrayList<>();
		for (final JassExpression expr : arguments) {
			final JassValue evaluatedExpression = expr.evaluate(globalScope, localScope);
			evaluatedExpressions.add(evaluatedExpression);
		}
		functionByName.call(evaluatedExpressions, globalScope);
		// throw away return value
		return null;
	}

}
