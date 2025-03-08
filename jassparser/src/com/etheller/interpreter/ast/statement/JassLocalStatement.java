package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassLocalStatement implements JassStatement {
	private final String identifier;
	private final JassTypeToken type;

	public JassLocalStatement(final String identifier, final JassTypeToken type) {
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

	public JassTypeToken getType() {
		return this.type;
	}

}
