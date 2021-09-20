package com.etheller.interpreter.ast.value;

import java.util.Arrays;

import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public class ArrayJassValue implements JassValue {
	private final JassValue[] data = new JassValue[8192]; // that's the array size in JASS
	private final ArrayJassType type;

	public ArrayJassValue(final ArrayJassType type) {
		this.type = type;
		final JassType primitiveType = this.type.getPrimitiveType();

		// Some default values for primitives... in general seems like the user script
		// should already do this, so maybe we could take it out for performance later,
		// but at the moment it's only a cost when we create a new array (local array)
		// which is rare. Anyway, preallocating arrays to size 8192 is very stupid.
		if (primitiveType == JassType.INTEGER) {
			Arrays.fill(this.data, IntegerJassValue.ZERO);
		}
		else if (primitiveType == JassType.REAL) {
			Arrays.fill(this.data, RealJassValue.ZERO);
		}
		else if (primitiveType == JassType.BOOLEAN) {
			Arrays.fill(this.data, BooleanJassValue.FALSE);
		}
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
