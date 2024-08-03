package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassIfStatement implements JassStatement {
	private final JassExpression condition;
	private final List<JassStatement> thenStatements;

	public JassIfStatement(final JassExpression condition, final List<JassStatement> thenStatements) {
		this.condition = condition;
		this.thenStatements = thenStatements;
	}

	public JassExpression getCondition() {
		return this.condition;
	}

	public List<JassStatement> getThenStatements() {
		return this.thenStatements;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
