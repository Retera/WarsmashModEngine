package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;

public interface JassType {
	<TYPE> TYPE visit(JassTypeVisitor<TYPE> visitor);

	String getName(); // used for error messages

	boolean isAssignableFrom(JassType value);

	boolean isNullable();

	JassValue getNullValue();

	public static final PrimitiveJassType INTEGER = new PrimitiveJassType("integer", IntegerJassValue.ZERO);
	public static final PrimitiveJassType STRING = new StringJassType("string");
	public static final PrimitiveJassType CODE = new CodeJassType("code");
	public static final PrimitiveJassType REAL = new RealJassType("real", RealJassValue.ZERO);
	public static final PrimitiveJassType BOOLEAN = new PrimitiveJassType("boolean", BooleanJassValue.FALSE);
	public static final PrimitiveJassType NOTHING = new PrimitiveJassType("nothing",
			JassReturnNothingStatement.RETURN_NOTHING_NOTICE);
	public static final PrimitiveJassType DUMMY = new PrimitiveJassType("dummy", DummyJassValue.PAUSE_FOR_SLEEP);
	public static final PrimitiveJassType ANY_STRUCT_TYPE = new AnyStructTypeJassType("structtype");
}
