package com.etheller.interpreter.ast.struct;

import java.util.EnumSet;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassStructMemberTypeDefinition {
	private final EnumSet<JassQualifier> qualifiers;
	private final JassTypeToken type;
	private final String id;
	private final JassExpression defaultValueExpression;

	public JassStructMemberTypeDefinition(final EnumSet<JassQualifier> qualifiers, final JassTypeToken type,
			final String id, final JassExpression defaultValueExpression) {
		this.qualifiers = qualifiers;
		this.type = type;
		this.id = id;
		this.defaultValueExpression = defaultValueExpression;
	}

	public EnumSet<JassQualifier> getQualifiers() {
		return this.qualifiers;
	}

	public JassTypeToken getType() {
		return this.type;
	}

	public String getId() {
		return this.id;
	}

	public JassExpression getDefaultValueExpression() {
		return this.defaultValueExpression;
	}
}
