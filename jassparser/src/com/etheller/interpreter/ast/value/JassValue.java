package com.etheller.interpreter.ast.value;

public interface JassValue {
	<TYPE> TYPE visit(JassValueVisitor<TYPE> visitor);
}
