package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;

public class DeclareLocalArrayInstruction implements JassInstruction {
	private final ArrayJassType arrayType;

	public DeclareLocalArrayInstruction(final ArrayJassType arrayType) {
		this.arrayType = arrayType;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(new ArrayJassValue(this.arrayType));
	}

}
