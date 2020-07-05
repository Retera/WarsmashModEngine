package com.etheller.interpreter.ast.scope.trigger;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public interface TriggerBooleanExpression {
	boolean evaluate(GlobalScope globalScope, TriggerExecutionScope triggerScope);
}
