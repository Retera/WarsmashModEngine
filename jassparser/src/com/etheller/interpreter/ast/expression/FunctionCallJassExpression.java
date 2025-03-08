package com.etheller.interpreter.ast.expression;

import java.util.List;

public class FunctionCallJassExpression implements JassExpression {
	private final String functionName;
	private final List<JassExpression> arguments;

	public FunctionCallJassExpression(final String functionName, final List<JassExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public <T> T accept(final JassExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public List<JassExpression> getArguments() {
		return this.arguments;
	}

}
