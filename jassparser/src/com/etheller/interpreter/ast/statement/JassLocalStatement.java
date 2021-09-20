package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.IntegerJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;

public class JassLocalStatement implements JassStatement {
	private final String identifier;
	private final JassType type;

	public JassLocalStatement(final String identifier, final JassType type) {
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		if (this.type == JassType.INTEGER) {
			localScope.createLocal(this.identifier, this.type, IntegerJassValue.ZERO);
		}
		else {
			localScope.createLocal(this.identifier, this.type);
		}
		return null;
	}

}
