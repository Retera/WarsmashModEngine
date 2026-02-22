package com.etheller.interpreter.ast.execution.instruction;

import com.etheller.interpreter.ast.execution.JassThread;

public class GlobalBeginFunctionInstruction extends BeginFunctionInstruction {
    private boolean isDefined = false; // Prevent from multiple JassProgram::initialize calls to re-register the already defined global variables.

    public GlobalBeginFunctionInstruction(final int lineNo, final String sourceFile, final String name) {
        super(lineNo, sourceFile, name);
    }

    @Override
    public void run(JassThread thread) {
        super.run(thread);
        if (isDefined) {
            thread.instructionPtr = thread.stackFrame.returnAddressInstructionPtr;
            return;
        }
        isDefined = true;
    }
}
