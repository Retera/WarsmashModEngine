package com.etheller.interpreter.ast.value;

public interface JassTypeVisitor<TYPE> {
	TYPE accept(PrimitiveJassType primitiveType);

	TYPE accept(ArrayJassType arrayType);

	TYPE accept(HandleJassType type);

	TYPE accept(StructJassType type);

	TYPE accept(StaticStructTypeJassValue staticType);
}
