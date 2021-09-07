package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StringJassValue;

public class JassReturnNothingStatement implements JassStatement {
	public static final StringJassValue RETURN_NOTHING_NOTICE = new StringJassValue("nothing");
	private final int lineNo;

	public JassReturnNothingStatement(final int lineNo) {
		this.lineNo = lineNo;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		globalScope.setLineNumber(this.lineNo);
		return RETURN_NOTHING_NOTICE;
	}

}
