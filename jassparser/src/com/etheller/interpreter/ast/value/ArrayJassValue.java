package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public class ArrayJassValue implements JassValue {
	private final JassValue[] data = new JassValue[8192]; // that's the array size in JASS
	private final ArrayJassType type;

	public ArrayJassValue(final ArrayJassType type) {
		this.type = type;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	public void set(final int index, final JassValue value) {
		if (value.visit(JassTypeGettingValueVisitor.getInstance()) != type.getPrimitiveType()) {
			throw new IllegalStateException(
					"Illegal type for assignment to " + type.getPrimitiveType().getName() + " array");
		}
		data[index] = value;
	}

	public JassValue get(final int index) {
		return data[index];
	}

	public ArrayJassType getType() {
		return type;
	}

}
