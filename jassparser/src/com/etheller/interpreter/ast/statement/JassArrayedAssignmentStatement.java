package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassArrayedAssignmentStatement implements JassStatement {
	private final String identifier;
	private final JassExpression indexExpression;
	private final JassExpression expression;

	public JassArrayedAssignmentStatement(final String identifier, final JassExpression indexExpression,
			final JassExpression expression) {
		this.identifier = identifier;
		this.indexExpression = indexExpression;
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassExpression getIndexExpression() {
		return this.indexExpression;
	}

	public JassExpression getExpression() {
		return this.expression;
	}
}
