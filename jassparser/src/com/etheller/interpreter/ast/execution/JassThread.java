package com.etheller.interpreter.ast.execution;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public class JassThread {
	public JassStackFrame stackFrame;
	public GlobalScope globalScope;
	public TriggerExecutionScope triggerScope;
	public int instructionPtr;
}
