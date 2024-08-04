package com.etheller.interpreter.ast.struct;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassStructMemberTypeDefinition {
	private final JassTypeToken type;
	private final String id;
	private final JassExpression defaultValueExpression;

	public JassStructMemberTypeDefinition(final JassTypeToken type, final String id,
			final JassExpression defaultValueExpression) {
		this.type = type;
		this.id = id;
		this.defaultValueExpression = defaultValueExpression;
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
