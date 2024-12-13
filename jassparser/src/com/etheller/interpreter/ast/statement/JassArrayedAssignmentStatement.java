package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassArrayedAssignmentStatement implements JassStatement {
	private final JassExpression identifierExpression;
	private final JassExpression indexExpression;
	private final JassExpression expression;

	public JassArrayedAssignmentStatement(final JassExpression identifierExpression,
			final JassExpression indexExpression, final JassExpression expression) {
		this.identifierExpression = identifierExpression;
		this.indexExpression = indexExpression;
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getIdentifierExpression() {
		return this.identifierExpression;
	}

	public JassExpression getIndexExpression() {
		return this.indexExpression;
	}

	public JassExpression getExpression() {
		return this.expression;
	}
}
