package com.etheller.interpreter.ast.scope.trigger;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public interface TriggerIntegerExpression {
	int evaluate(GlobalScope globalScope, TriggerExecutionScope triggerScope);
}
