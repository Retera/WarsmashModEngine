package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.StructJassValueVisitor;

public class StructMemberReferenceInstruction implements JassInstruction {
	private final int memberIndex;

	public StructMemberReferenceInstruction(final int memberIndex) {
		this.memberIndex = memberIndex;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue struct = thread.stackFrame.pop();
		final StructJassValue unwrappedStruct = struct.visit(StructJassValueVisitor.getInstance());
		thread.stackFrame.push(unwrappedStruct.getMember(this.memberIndex));
	}

}
