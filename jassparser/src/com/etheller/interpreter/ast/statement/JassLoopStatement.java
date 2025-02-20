package com.etheller.interpreter.ast.statement;

import java.util.List;

public class JassLoopStatement implements JassStatement {
	private final List<JassStatement> statements;

	public JassLoopStatement(final List<JassStatement> statements) {
		this.statements = statements;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public List<JassStatement> getStatements() {
		return this.statements;
	}

}
