package com.etheller.interpreter.ast.expression;

public interface JassExpressionVisitor<TYPE> {
	TYPE visit(ArithmeticJassExpression expression);

	TYPE visit(ArrayRefJassExpression expression);

	TYPE visit(FunctionCallJassExpression expression);

	TYPE visit(MethodCallJassExpression expression);

	TYPE visit(ParentlessMethodCallJassExpression expression);

	TYPE visit(FunctionReferenceJassExpression expression);

	TYPE visit(MethodReferenceJassExpression expression);

	TYPE visit(LiteralJassExpression expression);

	TYPE visit(NegateJassExpression expression);

	TYPE visit(NotJassExpression expression);

	TYPE visit(ReferenceJassExpression expression);

	TYPE visit(MemberJassExpression expression);

	TYPE visit(JassNewExpression expression);

	TYPE visit(AllocateAsNewTypeExpression expression);

	TYPE visit(ExtendHandleExpression expression);

	TYPE visit(TypeCastJassExpression expression);

}
