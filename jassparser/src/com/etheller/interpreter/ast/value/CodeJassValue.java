package com.etheller.interpreter.ast.value;

import java.util.Objects;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassStackFrame;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.JassSettings;

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
		final JassValue jassReturnValue;
		try {
			globalScope.runThreadUntilCompletion(thread);
			if (thread.instructionPtr == -1) {
				jassReturnValue = thread.stackFrame.getLast(0);
			}
			else {
				if (!JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
					throw new IllegalStateException("The " + contextNameForError
							+ " created a thread that did not immediately return; did you call TriggerSleepAction where it is unsupported??");
				}
				else {
					return defaultValue;
				}
			}
		}
		catch (final Exception e) {
			throw new JassException(globalScope, "Exception during " + contextNameForError + ".evaluate()", e);
		}
		return jassReturnValue;
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
