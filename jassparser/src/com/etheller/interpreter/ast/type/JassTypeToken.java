package com.etheller.interpreter.ast.type;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.value.JassType;

public interface JassTypeToken {
	JassType resolve(Scope globalScope);
}
