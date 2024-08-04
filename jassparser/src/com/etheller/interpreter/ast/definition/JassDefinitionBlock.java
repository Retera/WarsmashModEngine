package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.util.JassProgram;

public interface JassDefinitionBlock {
	void define(String mangledNameScope, JassProgram jassProgram);
}
