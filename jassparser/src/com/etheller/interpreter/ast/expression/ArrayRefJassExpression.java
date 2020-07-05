package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArrayJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;

public class ArrayRefJassExpression implements JassExpression {
	private final String identifier;
	private final JassExpression indexExpression;

	public ArrayRefJassExpression(final String identifier, final JassExpression indexExpression) {
		this.identifier = identifier;
		this.indexExpression = indexExpression;
	}

	@Override
	public JassValue evaluate(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		Assignable variable = localScope.getAssignableLocal(this.identifier);
		final JassValue index = this.indexExpression.evaluate(globalScope, localScope, triggerScope);
		if (variable == null) {
			variable = globalScope.getAssignableGlobal(this.identifier);
		}
		if (variable.getValue() == null) {
			throw new RuntimeException("Unable to use subscript on uninitialized variable");
		}
		final ArrayJassValue arrayValue = variable.getValue().visit(ArrayJassValueVisitor.getInstance());
		if (arrayValue != null) {
			return arrayValue.get(index.visit(IntegerJassValueVisitor.getInstance()));
		}
		else {
			throw new RuntimeException("Not an array");
		}
	}

}
