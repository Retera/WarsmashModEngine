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

	public void set(final int index, JassValue value) {
		final JassType primitiveType = this.type.getPrimitiveType();
		if (value == null) {
			if (primitiveType.isNullable()) {
				value = primitiveType.getNullValue();
			}
			else {
				throw new IllegalStateException(
						"Attempted to set " + this.type.getName() + " to null in array at index " + index + "!");
			}
		}
		if (value.visit(JassTypeGettingValueVisitor.getInstance()) != primitiveType) {
			throw new IllegalStateException("Illegal type for assignment to " + primitiveType.getName() + " array");
		}
		this.data[index] = value;
	}

	public JassValue get(final int index) {
		return this.data[index];
	}

	public ArrayJassType getType() {
		return this.type;
	}

}
