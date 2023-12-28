package com.etheller.interpreter.ast.execution;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.value.JassValue;

public class JassStackFrame {
	public String functionNameMetaData;
	public int returnAddressInstructionPtr;
	public JassStackFrame stackBase;
	public List<JassValue> contents = new ArrayList<>();

	public JassValue getLast(final int offset) {
		return this.contents.get(this.contents.size() - 1 - offset);
	}

	public void push(final JassValue value) {
		this.contents.add(value);
	}

	public JassValue pop() {
		final int lastIndex = this.contents.size() - 1;
		final JassValue jassValue = this.contents.get(lastIndex);
		this.contents.remove(lastIndex);
		return jassValue;
	}
}
