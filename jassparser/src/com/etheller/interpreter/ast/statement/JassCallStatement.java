package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassCallStatement implements JassStatement {
	private final String functionName;
	private final List<JassExpression> arguments;

	public JassCallStatement(final String functionName, final List<JassExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public List<JassExpression> getArguments() {
		return this.arguments;
	}

}
