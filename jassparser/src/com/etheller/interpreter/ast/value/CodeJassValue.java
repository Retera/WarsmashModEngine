package com.etheller.interpreter.ast.value;

import java.util.Objects;

import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public class CodeJassValue implements JassValue {
	private final Integer userFunctionInstructionPtr;

	public CodeJassValue(final Integer userFunctionInstructionPtr) {
		this.userFunctionInstructionPtr = userFunctionInstructionPtr;
	}

	public Integer getUserFunctionInstructionPtr() {
		return this.userFunctionInstructionPtr;
	}

	public void initStack(final JassStackFrame stackFrame) {
	}

	public final void call(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		final JassThread thread = globalScope.createThread(this, triggerScope);
		globalScope.queueThread(thread);
		return;
	}

	public final JassValue callAndExecuteCapturingReturnValue(final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope, final String contextNameForError, final JassValue defaultValue) {
		final JassThread thread = globalScope.createThreadCapturingReturnValue(this, triggerScope);
		return globalScope.runThreadUntilCompletionAndReadReturnValue(thread,
				contextNameForError, defaultValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.userFunctionInstructionPtr);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CodeJassValue other = (CodeJassValue) obj;
		return Objects.equals(this.userFunctionInstructionPtr, other.userFunctionInstructionPtr);
	}

	@Override
	public <TYPE> TYPE visit(final JassValueVisitor<TYPE> visitor) {
		return visitor.accept(this);
	}

}
