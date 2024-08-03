package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;

public class JassIfElseIfStatement implements JassStatement {
	private final JassExpression condition;
	private final List<JassStatement> thenStatements;
	private final JassStatement elseifTail;

	public JassIfElseIfStatement(final JassExpression condition, final List<JassStatement> thenStatements,
			final JassStatement elseifTail) {
		this.condition = condition;
		this.thenStatements = thenStatements;
		this.elseifTail = elseifTail;
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

	public JassStatement getElseifTail() {
		return this.elseifTail;
	}
}
