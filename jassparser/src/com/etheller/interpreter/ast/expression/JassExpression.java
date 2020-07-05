package com.etheller.interpreter.ast.expression;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public interface JassExpression {
	JassValue evaluate(GlobalScope globalScope, LocalScope localScope, TriggerExecutionScope triggerScope);
}
