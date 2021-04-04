package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;

public class BoolExprAnd implements TriggerBooleanExpression {
	private final TriggerBooleanExpression operandA;
	private final TriggerBooleanExpression operandB;

	public BoolExprAnd(final TriggerBooleanExpression operandA, final TriggerBooleanExpression operandB) {
		this.operandA = operandA;
		this.operandB = operandB;
	}

	@Override
	public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		return this.operandA.evaluate(globalScope, triggerScope) && this.operandB.evaluate(globalScope, triggerScope);
	}

}
