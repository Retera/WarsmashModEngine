package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.util.JassSettings;

public class JassFunctionDefinitionBlock extends JassCodeDefinitionBlock implements JassDefinitionBlock {

	public JassFunctionDefinitionBlock(final int lineNo, final String sourceFile, final String name,
			final UserJassFunction code) {
		super(lineNo, sourceFile, name, code);
	}

	@Override
	public void define(final JassProgram jassProgram) {
		jassProgram.globalScope.defineFunction(getLineNo(), getSourceFile(), getName(), getCode());
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Defining jass user function: " + this.getName());
		}
	}
}
