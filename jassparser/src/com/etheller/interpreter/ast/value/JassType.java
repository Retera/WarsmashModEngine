package com.etheller.interpreter.ast.value;

public interface JassType {
	<TYPE> TYPE visit(JassTypeVisitor<TYPE> visitor);

	String getName(); // used for error messages

	boolean isAssignableFrom(JassType value);

	boolean isNullable();

	JassValue getNullValue();

	public static final PrimitiveJassType INTEGER = new PrimitiveJassType("integer");
	public static final PrimitiveJassType STRING = new StringJassType("string");
	public static final PrimitiveJassType CODE = new PrimitiveJassType("code");
	public static final PrimitiveJassType REAL = new RealJassType("real");
	public static final PrimitiveJassType BOOLEAN = new PrimitiveJassType("boolean");
	public static final PrimitiveJassType NOTHING = new PrimitiveJassType("nothing");
}
