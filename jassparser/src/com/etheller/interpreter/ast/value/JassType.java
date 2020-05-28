package com.etheller.interpreter.ast.value;

public interface JassType {
	<TYPE> TYPE visit(JassTypeVisitor<TYPE> visitor);

	public static final PrimitiveJassType INTEGER = new PrimitiveJassType("integer");
	public static final PrimitiveJassType STRING = new PrimitiveJassType("string");
	public static final PrimitiveJassType CODE = new PrimitiveJassType("code");
	public static final PrimitiveJassType REAL = new PrimitiveJassType("real");
	public static final PrimitiveJassType BOOLEAN = new PrimitiveJassType("boolean");
	public static final PrimitiveJassType NOTHING = new PrimitiveJassType("nothing");
}
