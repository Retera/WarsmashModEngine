package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.ArrayJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ArrayJassValueVisitor;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;

public class ArrayReferenceInstruction implements JassInstruction {

	@Override
	public void run(final JassThread thread) {
		final JassValue indexValue = thread.stackFrame.pop();
		final JassValue referencedValue = thread.stackFrame.pop();
		if (referencedValue == null) {
			throw new RuntimeException("Unable to use subscript on uninitialized variable");
		}
		final ArrayJassValue arrayValue = referencedValue.visit(ArrayJassValueVisitor.getInstance());
		if (arrayValue != null) {
			thread.stackFrame.push(arrayValue.get(indexValue.visit(IntegerJassValueVisitor.getInstance())));
		}
		else {
			throw new RuntimeException("Not an array");
		}
	}

}
