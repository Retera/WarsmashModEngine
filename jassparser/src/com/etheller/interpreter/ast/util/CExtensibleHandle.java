package com.etheller.interpreter.ast.util;

import java.util.List;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;

public interface CExtensibleHandle {
	StructJassValue getStructValue();

	void setStructValue(StructJassValue value);

	default <T> T runMethod(final GlobalScope globalScope, final Integer idxVtable, final String contextNameForError,
			final List<JassValue> arguments) {
		return runMethod(globalScope, idxVtable, contextNameForError, arguments, ObjectJassValueVisitor.getInstance());
	}

	default <T> T runMethod(final GlobalScope globalScope, final Integer idxVtable, final String contextNameForError,
			final List<JassValue> arguments, final JassValueVisitor<T> visitor) {
		try {
			final StructJassValue structValue = getStructValue();
			final Integer instructionPtr = structValue.getType().getMethodTable().get(idxVtable);
			arguments.add(0, structValue);
			final JassThread thread = globalScope.createThreadCapturingReturnValue(instructionPtr, arguments,
					TriggerExecutionScope.EMPTY);
			final JassValue jassReturnValue = globalScope.runThreadUntilCompletionAndReadReturnValue(thread,
					contextNameForError, null);
			if (jassReturnValue == null) {
				return null;
			}
			return jassReturnValue.visit(visitor);
		}
		catch (final Exception exc) {
			JassLog.report(exc);
			throw exc;
		}
	}

	default void runMethodReturnNothing(final GlobalScope globalScope, final Integer idxVtable,
			final List<JassValue> arguments) {
		try {
			final Integer instructionPtr = getStructValue().getType().getMethodTable().get(idxVtable);
			arguments.add(0, getStructValue());
			final JassThread thread = globalScope.createThread(instructionPtr, arguments, TriggerExecutionScope.EMPTY);
			globalScope.runThreadUntilCompletion(thread);
		}
		catch (final Exception exc) {
			JassLog.report(exc);
			throw exc;
		}
	}
}
