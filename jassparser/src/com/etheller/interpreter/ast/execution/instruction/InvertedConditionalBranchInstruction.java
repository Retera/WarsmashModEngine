package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class InvertedConditionalBranchInstruction implements JassInstruction {
	private final int newInstructionPointer;

	public InvertedConditionalBranchInstruction(final int newInstructionPointer) {
		this.newInstructionPointer = newInstructionPointer;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue topStackValue = thread.stackFrame.pop();
		if (!topStackValue.visit(BooleanJassValueVisitor.getInstance())) {
			thread.instructionPtr = this.newInstructionPointer;
		}
	}
}
