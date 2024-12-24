package com.etheller.interpreter.ast;

import com.etheller.interpreter.ast.util.JassSettings;
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
		if (value == null) {
			if (!this.type.isNullable()) {
				throw new RuntimeException("Type " + this.type.getName() + " cannot be assigned to null!");
			}
			this.value = this.type.getNullValue();
		}
		else {
			final JassType valueType = value.visit(JassTypeGettingValueVisitor.getInstance());
			if (!this.type.isAssignableFrom(valueType) && JassSettings.CHECK_TYPES) {
				throw new RuntimeException("Incompatible types " + valueType.getName() + " != " + this.type.getName());
			}
			this.value = value;
		}
	}

	public JassValue getValue() {
		return this.value;
	}

	public JassType getType() {
		return this.type;
	}
}
