package com.etheller.interpreter.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.JassValue;

public class JassCallStatement implements JassStatement {
	private final String functionName;
	private final List<JassExpression> arguments;

	public JassCallStatement(final String functionName, final List<JassExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final JassFunction functionByName = globalScope.getFunctionByName(this.functionName);
		if (functionByName == null) {
			if (JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
				System.err.println("Undefined function: " + this.functionName);
			}
			else {
				throw new RuntimeException("Undefined function: " + this.functionName);
			}
			return null;
		}
		final List<JassValue> evaluatedExpressions = new ArrayList<>();
		for (final JassExpression expr : this.arguments) {
			final JassValue evaluatedExpression = expr.evaluate(globalScope, localScope, triggerScope);
			evaluatedExpressions.add(evaluatedExpression);
		}
		functionByName.call(evaluatedExpressions, globalScope, triggerScope);
		// throw away return value
		return null;
	}

}
