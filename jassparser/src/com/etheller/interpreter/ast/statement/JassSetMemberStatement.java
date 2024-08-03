package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassSetMemberStatement implements JassStatement {
	private final JassExpression structExpression;
	private final String identifier;
	private final JassExpression expression;

	public JassSetMemberStatement(final JassExpression structExpression, final String identifier,
			final JassExpression expression) {
		this.structExpression = structExpression;
		this.identifier = identifier;
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getStructExpression() {
		return this.structExpression;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassExpression getExpression() {
		return this.expression;
	}
}
