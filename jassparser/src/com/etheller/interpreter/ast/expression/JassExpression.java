package com.etheller.interpreter.ast.expression;

public interface JassExpression {
	<T> T accept(JassExpressionVisitor<T> visitor);
}
