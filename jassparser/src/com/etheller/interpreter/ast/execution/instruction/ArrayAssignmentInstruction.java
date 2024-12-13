package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArrayJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;

public class ArrayAssignmentInstruction implements JassInstruction {

	public static final ArrayAssignmentInstruction INSTANCE = new ArrayAssignmentInstruction();

	public ArrayAssignmentInstruction() {
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue value = thread.stackFrame.pop();
		final JassValue index = thread.stackFrame.pop();
		final JassValue array = thread.stackFrame.pop();

		if (array == null) {
			throw new JassException(thread.globalScope, "Unable to assign uninitialized array", null);
		}
		final ArrayJassValue arrayValue = array.visit(ArrayJassValueVisitor.getInstance());
		if (arrayValue != null) {
			final Integer indexInt = index.visit(IntegerJassValueVisitor.getInstance());
			if ((indexInt != null) && (indexInt >= 0)) {
				arrayValue.set(thread.globalScope, indexInt, value);
			}
			else {
				throw new JassException(thread.globalScope,
						"Attempted to assign " + array + "[" + indexInt + "], which was an illegal index", null);
			}
		}
		else {
			throw new JassException(thread.globalScope, "Not an array", null);
		}

	}

}
