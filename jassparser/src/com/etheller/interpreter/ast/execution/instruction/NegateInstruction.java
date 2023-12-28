package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.visitor.NegateJassValueVisitor;

public class NegateInstruction implements JassInstruction {

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(thread.stackFrame.pop().visit(NegateJassValueVisitor.getInstance()));
	}

}
