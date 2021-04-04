package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public interface JassStatement {
	// When a value is returned, this indicates a RETURN statement,
	// and will end outer execution
	JassValue execute(GlobalScope globalScope, LocalScope localScope, TriggerExecutionScope triggerScope);
}
