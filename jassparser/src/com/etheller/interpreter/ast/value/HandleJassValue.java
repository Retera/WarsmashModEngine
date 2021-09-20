package com.etheller.interpreter.ast.value;

public class HandleJassValue implements JassValue {
	private final HandleJassType type;
	private final Object javaValue;

	public HandleJassValue(final HandleJassType type, final Object javaValue) {
		this.type = type;
		this.javaValue = javaValue;
	}

	public HandleJassType getType() {
		return this.type;
	}

	public Object getJavaValue() {
		return this.javaValue;
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

	@Override
	public String toString() {
		return this.type.getName() + ":" + this.javaValue;
	}

}
