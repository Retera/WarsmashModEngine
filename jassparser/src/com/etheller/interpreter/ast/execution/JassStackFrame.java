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
		return this.contents.get(this.contents.size() - 1 - offset);
	}

	public void push(final JassValue value) {
		this.contents.add(value);
	}

	public JassValue pop() {
		final JassValue jassValue = this.contents.get(this.contents.size() - 1);
		this.contents.remove(this.contents.size() - 1);
		return jassValue;
	}

	public JassValue peek() {
		return getLast(0);
	}
}
