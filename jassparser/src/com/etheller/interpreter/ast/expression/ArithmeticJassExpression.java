package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArithmeticJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.ArithmeticLeftHandNullJassValueVisitor;

public class ArithmeticJassExpression implements JassExpression {

	private final JassExpression leftExpression;
	private final JassExpression rightExpression;
	private final ArithmeticSign arithmeticSign;

	public ArithmeticJassExpression(final JassExpression leftExpression, final JassExpression rightExpression,
			final ArithmeticSign arithmeticSign) {
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
		this.arithmeticSign = arithmeticSign;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final JassValue leftValue = this.leftExpression.evaluate(globalScope, localScope, triggerScope);
		final JassValue rightValue = this.rightExpression.evaluate(globalScope, localScope, triggerScope);
		try {
			if (leftValue == null) {
				if (rightValue == null) {
					return this.arithmeticSign.apply((String) null, (String) null);
				}
				else {
					return rightValue.visit(ArithmeticLeftHandNullJassValueVisitor.INSTANCE.reset(this.arithmeticSign));
				}
			}
			return leftValue.visit(ArithmeticJassValueVisitor.INSTANCE.reset(rightValue, this.arithmeticSign));
		}
		catch (final ArithmeticException exception) {
			exception.printStackTrace();
			return IntegerJassValue.ZERO;
		}
	}
}
