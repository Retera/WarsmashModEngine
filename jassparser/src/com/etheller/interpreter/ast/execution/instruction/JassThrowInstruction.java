package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;

public class JassThrowInstruction implements JassInstruction {
	private final String message;

	public JassThrowInstruction(final String message) {
		this.message = message;
	}

	@Override
	public void run(final JassThread thread) {
		throw new JassException(thread.globalScope, this.message, null);
	}

}
