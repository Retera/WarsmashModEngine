package com.etheller.interpreter.ast.function;

import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public class JassParameter {
	private final JassType type;
	private final String identifier;

	public JassParameter(final JassType type, final String identifier) {
		this.type = type;
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public JassType getType() {
		return type;
	}

	public boolean matchesType(final JassValue value) {
		return type == value.visit(JassTypeGettingValueVisitor.getInstance());
	}
}
