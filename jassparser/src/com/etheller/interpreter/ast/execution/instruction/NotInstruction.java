package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.value.visitor.NotJassValueVisitor;

public class NotInstruction implements JassInstruction {

	@Override
	public void run(final JassThread thread) {
		thread.stackFrame.push(thread.stackFrame.pop().visit(NotJassValueVisitor.getInstance()));
	}

}
