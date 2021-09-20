package com.etheller.interpreter.ast.debug;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.value.JassValue;

public class DebuggingJassStatement implements JassStatement {
	private final int lineNo;
	private final JassStatement delegate;

	public DebuggingJassStatement(final int lineNo, final JassStatement delegate) {
		this.lineNo = lineNo;
		this.delegate = delegate;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		globalScope.setLineNumber(this.lineNo);
		return this.delegate.execute(globalScope, localScope, triggerScope);
	}

}
