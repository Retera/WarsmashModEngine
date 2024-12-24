package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassIfElseStatement implements JassStatement {
	private final JassExpression condition;
	private final List<JassStatement> thenStatements;
	private final List<JassStatement> elseStatements;

	public JassIfElseStatement(final JassExpression condition, final List<JassStatement> thenStatements,
			final List<JassStatement> elseStatements) {
		this.condition = condition;
		this.thenStatements = thenStatements;
		this.elseStatements = elseStatements;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getCondition() {
		return this.condition;
	}

	public List<JassStatement> getThenStatements() {
		return this.thenStatements;
	}

	public List<JassStatement> getElseStatements() {
		return this.elseStatements;
	}

}
