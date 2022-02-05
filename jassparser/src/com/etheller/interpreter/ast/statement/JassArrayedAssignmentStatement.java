package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArrayJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;

public class JassArrayedAssignmentStatement implements JassStatement {
	private final String identifier;
	private final JassExpression indexExpression;
	private final JassExpression expression;

	public JassArrayedAssignmentStatement(final String identifier, final JassExpression indexExpression,
			final JassExpression expression) {
		this.identifier = identifier;
		this.indexExpression = indexExpression;
		this.expression = expression;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		Assignable variable = localScope.getAssignableLocal(this.identifier);
		final JassValue index = this.indexExpression.evaluate(globalScope, localScope, triggerScope);
		if (variable == null) {
			variable = globalScope.getAssignableGlobal(this.identifier);
		}
		if (variable.getValue() == null) {
			throw new JassException(globalScope, "Unable to assign uninitialized array", null);
		}
		final ArrayJassValue arrayValue = variable.getValue().visit(ArrayJassValueVisitor.getInstance());
		if (arrayValue != null) {
			final Integer indexInt = index.visit(IntegerJassValueVisitor.getInstance());
			if ((indexInt != null) && (indexInt >= 0)) {
				arrayValue.set(globalScope, indexInt, this.expression.evaluate(globalScope, localScope, triggerScope));
			}
			else {
				throw new JassException(globalScope,
						"Attempted to assign " + this.identifier + "[" + indexInt + "], which was an illegal index",
						null);
			}
		}
		else {
			throw new JassException(globalScope, "Not an array", null);
		}
		return null;
	}

}
