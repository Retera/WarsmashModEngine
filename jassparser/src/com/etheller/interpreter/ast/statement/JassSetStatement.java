package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public class JassSetStatement implements JassStatement {
	private final String identifier;
	private final JassExpression expression;

	public JassSetStatement(final String identifier, final JassExpression expression) {
		this.identifier = identifier;
		this.expression = expression;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final Assignable local = localScope.getAssignableLocal(this.identifier);
		if (local != null) {
			local.setValue(this.expression.evaluate(globalScope, localScope, triggerScope));
		}
		else {
			globalScope.setGlobal(this.identifier, this.expression.evaluate(globalScope, localScope, triggerScope));
		}
		return null;
	}

}
