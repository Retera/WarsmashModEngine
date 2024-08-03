package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.function.UserJassFunction;

public class JassCodeDefinitionBlock {
	private final int lineNo;
	private final String sourceFile;
	private final String name;
	private final UserJassFunction code;

	public JassCodeDefinitionBlock(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction code) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.name = name;
		this.code = code;
	}

	public int getLineNo() {
		return this.lineNo;
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public String getName() {
		return this.name;
	}

	public UserJassFunction getCode() {
		return this.code;
	}
}
