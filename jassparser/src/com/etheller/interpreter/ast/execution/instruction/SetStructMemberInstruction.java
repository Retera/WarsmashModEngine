package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.BaseStructJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BaseStructJassValueVisitor;

public class SetStructMemberInstruction implements JassInstruction {
	private final int memberIndex;

	public SetStructMemberInstruction(final int memberIndex) {
		this.memberIndex = memberIndex;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue value = thread.stackFrame.pop();
		final BaseStructJassValue struct = thread.stackFrame.pop().visit(BaseStructJassValueVisitor.getInstance());
		struct.setMember(this.memberIndex, value);
	}

}
