package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;

public class JassLocalDefinitionStatement implements JassStatement {
	private final String identifier;
	private final JassExpression expression;
	private final JassType type;

	public JassLocalDefinitionStatement(final String identifier, final JassType type, final JassExpression expression) {
		this.identifier = identifier;
		this.type = type;
		this.expression = expression;
	}

	@Override
	public JassValue execute(final GlobalScope globalScope, final LocalScope localScope,
			final TriggerExecutionScope triggerScope) {
		localScope.createLocal(this.identifier, this.type,
				this.expression.evaluate(globalScope, localScope, triggerScope));
		return null;
	}

}
