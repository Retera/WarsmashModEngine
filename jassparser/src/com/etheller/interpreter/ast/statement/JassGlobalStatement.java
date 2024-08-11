package com.etheller.interpreter.ast.statement;

import java.util.EnumSet;

import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassGlobalStatement implements JassStatement {
	private final String identifier;
	private final JassTypeToken type;
	private final EnumSet<JassQualifier> qualifiers;

	public JassGlobalStatement(final EnumSet<JassQualifier> qualifiers, final String identifier,
			final JassTypeToken type) {
		this.qualifiers = qualifiers;
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public EnumSet<JassQualifier> getQualifiers() {
		return this.qualifiers;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassTypeToken getType() {
		return this.type;
	}

}
