package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.BaseStructJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BaseStructJassValueVisitor;

public class StructMemberReferenceInstruction implements JassInstruction {
	private final int memberIndex;

	public StructMemberReferenceInstruction(final int memberIndex) {
		this.memberIndex = memberIndex;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue struct = thread.stackFrame.pop();
		final BaseStructJassValue unwrappedStruct = struct.visit(BaseStructJassValueVisitor.getInstance());
		thread.stackFrame.push(unwrappedStruct.getMember(this.memberIndex));
	}

}
