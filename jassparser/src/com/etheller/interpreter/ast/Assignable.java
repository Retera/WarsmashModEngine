package com.etheller.interpreter.ast;

import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public class Assignable {
	private JassValue value;
	private final JassType type;

	public Assignable(final JassType type) {
		this.type = type;
	}

	public void setValue(final JassValue value) {
		if (value.visit(JassTypeGettingValueVisitor.getInstance()) != type) {
			throw new RuntimeException("Incompatible types");
		}
		this.value = value;
	}

	public JassValue getValue() {
		return value;
	}

	public JassType getType() {
		return type;
	}
}
