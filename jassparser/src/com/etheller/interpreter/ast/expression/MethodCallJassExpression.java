package com.etheller.interpreter.ast.expression;

import java.util.List;

public class MethodCallJassExpression implements JassExpression {
	private final JassExpression structExpression;
	private final String functionName;
	private final List<JassExpression> arguments;

	public MethodCallJassExpression(final JassExpression structExpression, final String functionName,
			final List<JassExpression> arguments) {
		this.structExpression = structExpression;
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getStructExpression() {
		return this.structExpression;
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public List<JassExpression> getArguments() {
		return this.arguments;
	}

}
