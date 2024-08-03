package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.function.JassFunction;

public class HandleJassTypeConstructor {
	private final String name;
	private final JassFunction nativeCode;

	public HandleJassTypeConstructor(final String name, final JassFunction nativeCode) {
		this.name = name;
		this.nativeCode = nativeCode;
	}

	public String getName() {
		return this.name;
	}

	public JassFunction getNativeCode() {
		return this.nativeCode;
	}
}
