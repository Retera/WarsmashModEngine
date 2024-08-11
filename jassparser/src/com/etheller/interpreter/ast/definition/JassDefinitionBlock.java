package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.util.JassProgram;

public interface JassDefinitionBlock {
	void define(Scope scope, JassProgram jassProgram);
}
