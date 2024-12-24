package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public interface JassInstruction {
	void run(JassThread thread);
}
