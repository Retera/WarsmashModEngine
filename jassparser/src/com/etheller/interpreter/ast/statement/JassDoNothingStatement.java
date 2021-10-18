package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public class JassDoNothingStatement implements JassStatement {

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		return null;
	}

}
