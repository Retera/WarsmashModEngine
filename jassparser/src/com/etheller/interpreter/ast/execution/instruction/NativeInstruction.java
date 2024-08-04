package com.etheller.interpreter.ast.execution.instruction;

import java.util.LinkedList;

import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.function.NativeJassFunction;
import com.etheller.interpreter.ast.value.JassValue;

public class NativeInstruction implements JassInstruction {
	private final int nativeId;
	private final int argumentCount;

	public NativeInstruction(final int nativeId, final int argumentCount) {
		this.nativeId = nativeId;
		this.argumentCount = argumentCount;
	}

	@Override
	public void run(final JassThread thread) {
		final NativeJassFunction nativeFromId = thread.globalScope.getNativeById(this.nativeId);
		final LinkedList<JassValue> arguments = new LinkedList<>();
		for (int i = 0; i < this.argumentCount; i++) {
			arguments.addFirst(thread.stackFrame.pop());
		}
		final JassValue nativeReturnValue = nativeFromId.call(arguments, thread.globalScope, thread.triggerScope);
		thread.stackFrame.push(nativeReturnValue);
		ReturnInstruction.INSTANCE.run(thread);

	}
}
