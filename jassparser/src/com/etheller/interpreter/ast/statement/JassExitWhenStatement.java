package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StringJassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class JassExitWhenStatement implements JassStatement {
	public static final StringJassValue LOOP_EXIT_NOTICE = new StringJassValue("EXIT");
	private final int lineNo;
	private final JassExpression expression;

	public JassExitWhenStatement(final int lineNo, final JassExpression expression) {
		this.lineNo = lineNo;
		this.expression = expression;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		globalScope.setLineNumber(this.lineNo);
		if (this.expression.evaluate(globalScope, localScope, triggerScope)
				.visit(BooleanJassValueVisitor.getInstance())) {
			return LOOP_EXIT_NOTICE;
		}
		return null;
	}

}
