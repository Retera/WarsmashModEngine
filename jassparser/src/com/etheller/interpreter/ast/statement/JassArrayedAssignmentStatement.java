package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.Assignable;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
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
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope) {
		Assignable variable = localScope.getAssignableLocal(identifier);
		final JassValue index = indexExpression.evaluate(globalScope, localScope);
		if (variable == null) {
			variable = globalScope.getAssignableGlobal(identifier);
		}
		if (variable.getValue() == null) {
			throw new RuntimeException("Unable to assign uninitialized array");
		}
		final ArrayJassValue arrayValue = variable.getValue().visit(ArrayJassValueVisitor.getInstance());
		if (arrayValue != null) {
			arrayValue.set(index.visit(IntegerJassValueVisitor.getInstance()),
					expression.evaluate(globalScope, localScope));
		} else {
			throw new RuntimeException("Not an array");
		}
		return null;
	}

}
