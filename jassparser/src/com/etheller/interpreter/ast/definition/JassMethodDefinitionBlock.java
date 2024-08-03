package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.function.UserJassFunction;

public class JassMethodDefinitionBlock extends JassCodeDefinitionBlock {
	private final boolean staticMethod;

	public JassMethodDefinitionBlock(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction code, final boolean staticMethod) {
		super(lineNo, sourceFile, name, code);
		this.staticMethod = staticMethod;
	}

	public boolean isStaticMethod() {
		return this.staticMethod;
	}
}
