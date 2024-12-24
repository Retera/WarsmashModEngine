package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.JassValueVisitor;

public class TypeCastInstruction implements JassInstruction {
	private final JassValueVisitor<JassValue> typeConverter;
	private final JassValue typedNull;

	public TypeCastInstruction(final JassValueVisitor<JassValue> typeConverter, final JassValue typedNull) {
		this.typeConverter = typeConverter;
		this.typedNull = typedNull;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue value = thread.stackFrame.pop();
		if (value == null) {
			thread.stackFrame.push(this.typedNull);
		}
		else {
			thread.stackFrame.push(value.visit(this.typeConverter));
		}
	}
}
