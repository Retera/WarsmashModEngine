package com.etheller.interpreter.ast.statement;

import java.util.List;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class JassIfStatement implements JassStatement {
	private final JassExpression condition;
	private final List<JassStatement> thenStatements;

	public JassIfStatement(final JassExpression condition, final List<JassStatement> thenStatements) {
		this.condition = condition;
		this.thenStatements = thenStatements;
	}

	public JassExpression getCondition() {
		return this.condition;
	}

	public List<JassStatement> getThenStatements() {
		return this.thenStatements;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		final JassValue evaluate = this.condition.evaluate(globalScope, localScope, triggerScope);
		// TODO this null is here for simulations where we are missing natives, remove
		// it on full release
		if ((evaluate != null) && evaluate.visit(BooleanJassValueVisitor.getInstance())) {
			for (final JassStatement statement : this.thenStatements) {
				final JassValue returnValue = statement.execute(globalScope, localScope, triggerScope);
				if (returnValue != null) {
					return returnValue;
				}
			}
		}
		return null;
	}

}
