package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;

public class FunctionReferenceJassExpression implements JassExpression {
	private final String identifier;

	public FunctionReferenceJassExpression(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final JassFunction functionByName = globalScope.getFunctionByName(this.identifier);
		final Integer userFunctionInstructionPtr = globalScope.getUserFunctionInstructionPtr(this.identifier);
		if ((functionByName == null) || (userFunctionInstructionPtr == null)) {
			throw new RuntimeException("Unable to find function: " + this.identifier);
		}
		return new CodeJassValue(functionByName, userFunctionInstructionPtr);
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}
}
