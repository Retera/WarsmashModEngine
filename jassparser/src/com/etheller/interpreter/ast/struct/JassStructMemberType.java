package com.etheller.interpreter.ast.struct;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.value.JassType;

public class JassStructMemberType {
	private final JassType type;
	private final String id;
	private final JassExpression defaultValueExpression;

	public JassStructMemberType(final JassType type, final String id, final JassExpression defaultValueExpression) {
		this.type = type;
		this.id = id;
		this.defaultValueExpression = defaultValueExpression;
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
