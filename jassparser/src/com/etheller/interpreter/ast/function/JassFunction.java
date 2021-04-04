package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public interface JassFunction {
	JassValue call(List<JassValue> arguments, GlobalScope globalScope, TriggerExecutionScope triggerScope);
}
