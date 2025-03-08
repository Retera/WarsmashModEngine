package com.etheller.interpreter.ast.execution;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public class JassThread {
	public JassStackFrame stackFrame;
	public GlobalScope globalScope;
	public TriggerExecutionScope triggerScope;
	public int instructionPtr;
	public boolean sleeping = false;
	public JassThread parent;

	public JassThread(final JassStackFrame stackFrame, final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope, final int instructionPtr) {
		this.stackFrame = stackFrame;
		this.globalScope = globalScope;
		this.triggerScope = triggerScope;
		this.instructionPtr = instructionPtr;
	}

	public void setSleeping(final boolean sleeping) {
		this.sleeping = sleeping;
	}

	public boolean isSleeping() {
		return this.sleeping;
	}
}
