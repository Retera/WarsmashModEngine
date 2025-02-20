package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.MethodJassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class MethodReferenceInstruction implements JassInstruction {
	private final int methodTableIndex;

	public MethodReferenceInstruction(final int methodTableIndex) {
		this.methodTableIndex = methodTableIndex;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue struct = thread.stackFrame.pop();
		final JassType jassType = struct.visit(JassTypeGettingValueVisitor.getInstance());
		final StructJassType structType = jassType.visit(StructJassTypeVisitor.getInstance());
		final int instructionPtr = structType.getMethodTable().get(this.methodTableIndex);
		thread.stackFrame.push(new MethodJassValue(struct, instructionPtr));
	}

}
