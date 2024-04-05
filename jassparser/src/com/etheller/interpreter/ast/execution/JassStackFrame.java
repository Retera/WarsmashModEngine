package com.etheller.interpreter.ast.execution;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.execution.instruction.BeginFunctionInstruction;
import com.etheller.interpreter.ast.value.JassValue;

public class JassStackFrame {
	public BeginFunctionInstruction functionNameMetaData;
	public int returnAddressInstructionPtr;
	public JassStackFrame stackBase;
	public List<JassValue> contents;
	public int debugLineNo;

	public JassStackFrame(final int argumentCount) {
		this.contents = new ArrayList<>(argumentCount);
	}

	public JassStackFrame() {
		this.contents = new ArrayList<>();
	}

	public JassValue getLast(final int offset) {
		final int index = this.contents.size() - 1 - offset;
		if ((index >= this.contents.size()) || (index < 0)) {
			System.err.println("bad");
		}
		return this.contents.get(index);
	}

	public void push(final JassValue value) {
		this.contents.add(value);
	}

	public JassValue pop() {
		final int lastIndex = this.contents.size() - 1;
		if (lastIndex == -1) {
			System.err.println("bad");
		}
		final JassValue jassValue = this.contents.get(lastIndex);
		this.contents.remove(lastIndex);
		return jassValue;
	}
}
