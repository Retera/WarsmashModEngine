package com.etheller.interpreter.ast.statement;

import java.util.EnumSet;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassGlobalDefinitionStatement implements JassStatement {
	private final EnumSet<JassQualifier> qualifiers;
	private final String identifier;
	private final JassExpression expression;
	private final JassTypeToken type;

	public JassGlobalDefinitionStatement(final EnumSet<JassQualifier> qualifiers, final String identifier,
			final JassTypeToken type, final JassExpression expression) {
		this.qualifiers = qualifiers;
		this.identifier = identifier;
		this.type = type;
		this.expression = expression;
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

	public JassExpression getExpression() {
		return this.expression;
	}

	public JassTypeToken getType() {
		return this.type;
	}

}
