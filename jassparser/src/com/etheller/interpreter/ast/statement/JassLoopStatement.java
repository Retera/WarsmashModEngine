package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public class JassLoopStatement implements JassStatement {
	private final List<JassStatement> statements;

	public JassLoopStatement(final List<JassStatement> statements) {
		this.statements = statements;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		while (true) {
			for (final JassStatement statement : this.statements) {
				final JassValue returnValue = statement.execute(globalScope, localScope, triggerScope);
				if (returnValue != null) {
					if (returnValue == JassExitWhenStatement.LOOP_EXIT_NOTICE) {
						return null;
					}
					else {
						return returnValue;
					}
				}
			}
		}
	}

}
