package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.StructJassValueVisitor;

public class AllocateStructAsNewTypeInstruction implements JassInstruction {
	private final StructJassType childType;

	public AllocateStructAsNewTypeInstruction(final StructJassType childType) {
		this.childType = childType;
	}

	@Override
	public void run(final JassThread thread) {
		final JassValue structAsJassValue = thread.stackFrame.getLast(0);
		final StructJassValue struct = structAsJassValue.visit(StructJassValueVisitor.getInstance());
		struct.allocateAsNewType(this.childType);
	}

}
