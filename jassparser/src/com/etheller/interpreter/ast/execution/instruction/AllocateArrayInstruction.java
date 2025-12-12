package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.ArrayJassType;
import com.etheller.interpreter.ast.value.ArrayJassValue;

public class AllocateArrayInstruction implements JassInstruction {
	private final ArrayJassType jassType;

	public AllocateArrayInstruction(final ArrayJassType jassType) {
		this.jassType = jassType;
	}

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(new ArrayJassValue(this.jassType));
	}

}
