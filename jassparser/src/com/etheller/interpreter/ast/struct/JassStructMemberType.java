package com.etheller.interpreter.ast.struct;

import java.util.EnumSet;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.value.JassType;

public class JassStructMemberType {
	private final EnumSet<JassQualifier> qualifiers;
	private final JassType type;
	private final String id;
	private final JassExpression defaultValueExpression;

	public JassStructMemberType(final EnumSet<JassQualifier> qualifiers, final JassType type, final String id,
			final JassExpression defaultValueExpression) {
		this.qualifiers = qualifiers;
		this.type = type;
		this.id = id;
		this.defaultValueExpression = defaultValueExpression;
	}

	public EnumSet<JassQualifier> getQualifiers() {
		return this.qualifiers;
	}

	public JassType getType() {
		return this.type;
	}

	public String getId() {
		return this.id;
	}

	public JassExpression getDefaultValueExpression() {
		return this.defaultValueExpression;
	}
}
