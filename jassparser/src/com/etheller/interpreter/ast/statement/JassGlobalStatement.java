package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.value.JassType;

public class JassGlobalStatement implements JassStatement {
	private final String identifier;
	private final JassType type;

	public JassGlobalStatement(final String identifier, final JassType type) {
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassType getType() {
		return this.type;
	}

}
