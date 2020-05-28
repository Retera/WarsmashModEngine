package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassValue;

public class ReferenceJassExpression implements JassExpression {
	private final String identifier;

	public ReferenceJassExpression(final String identifier) {
		this.identifier = identifier;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope) {
		final Assignable local = localScope.getAssignableLocal(identifier);
		if (local == null) {
			return globalScope.getGlobal(identifier);
		}
		return local.getValue();
	}

}
