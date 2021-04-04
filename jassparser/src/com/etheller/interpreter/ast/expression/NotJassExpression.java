package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.NotJassValueVisitor;

public class NotJassExpression implements JassExpression {
	private final JassExpression expression;

	public NotJassExpression(final JassExpression expression) {
		this.expression = expression;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		return this.expression.evaluate(globalScope, localScope, triggerScope).visit(NotJassValueVisitor.getInstance());
	}
}
