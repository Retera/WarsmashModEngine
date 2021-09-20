package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class JassIfElseIfStatement implements JassStatement {
	private final JassExpression condition;
	private final List<JassStatement> thenStatements;
	private final JassStatement elseifTail;

	public JassIfElseIfStatement(final JassExpression condition, final List<JassStatement> thenStatements,
			final JassStatement elseifTail) {
		this.condition = condition;
		this.thenStatements = thenStatements;
		this.elseifTail = elseifTail;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		if (this.condition.evaluate(globalScope, localScope, triggerScope)
				.visit(BooleanJassValueVisitor.getInstance())) {
			for (final JassStatement statement : this.thenStatements) {
				final JassValue returnValue = statement.execute(globalScope, localScope, triggerScope);
				if (returnValue != null) {
					return returnValue;
				}
			}
		}
		else {
			return this.elseifTail.execute(globalScope, localScope, triggerScope);
		}
		return null;
	}

}
