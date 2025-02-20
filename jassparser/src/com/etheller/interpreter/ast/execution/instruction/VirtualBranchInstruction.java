package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class VirtualBranchInstruction implements JassInstruction {

	private final int stackIndex;
	private final int methodTableIndex;

	public VirtualBranchInstruction(final int stackIndex, final int methodTableIndex) {
		this.stackIndex = stackIndex;
		this.methodTableIndex = methodTableIndex;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue struct = thread.stackFrame.getLast(this.stackIndex);
		final JassType jassType = struct.visit(JassTypeGettingValueVisitor.getInstance());
		final StructJassType structType = jassType.visit(StructJassTypeVisitor.getInstance());
		thread.instructionPtr = structType.getMethodTable().get(this.methodTableIndex);
	}
}
