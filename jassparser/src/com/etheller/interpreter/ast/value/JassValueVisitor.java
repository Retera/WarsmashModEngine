package com.etheller.interpreter.ast.value;

public interface JassValueVisitor<TYPE> {
	TYPE accept(IntegerJassValue value);

	TYPE accept(RealJassValue value);

	TYPE accept(BooleanJassValue value);

	TYPE accept(StringJassValue value);

	TYPE accept(CodeJassValue value);

	TYPE accept(ArrayJassValue value);

	TYPE accept(HandleJassValue value);

	TYPE accept(DummyJassValue value);

	TYPE accept(StructJassValue value);

	TYPE accept(StaticStructTypeJassValue value);
}
