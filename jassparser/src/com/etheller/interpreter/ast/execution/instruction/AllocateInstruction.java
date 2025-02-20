package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.StructJassType;
import com.etheller.interpreter.ast.value.StructJassValue;
import com.etheller.interpreter.ast.value.visitor.StructJassTypeVisitor;

public class AllocateInstruction implements JassInstruction {
	private final JassType jassType;

	public AllocateInstruction(final JassType jassType) {
		this.jassType = jassType;
	}

	@Override
	public void run(final JassThread thread) {
		final StructJassType structJassType = this.jassType.visit(StructJassTypeVisitor.getInstance());
		thread.stackFrame.push(new StructJassValue(structJassType, null));
	}

}
