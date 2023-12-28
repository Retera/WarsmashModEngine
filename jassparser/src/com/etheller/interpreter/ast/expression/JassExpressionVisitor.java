package com.etheller.interpreter.ast.expression;

public interface JassExpressionVisitor<TYPE> {
	TYPE visit(ArithmeticJassExpression expression);

	TYPE visit(ArrayRefJassExpression expression);

	TYPE visit(FunctionCallJassExpression expression);

	TYPE visit(FunctionReferenceJassExpression expression);

	TYPE visit(LiteralJassExpression expression);

	TYPE visit(NegateJassExpression expression);

	TYPE visit(NotJassExpression expression);

	TYPE visit(ReferenceJassExpression expression);

}
