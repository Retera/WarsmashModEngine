package com.etheller.interpreter.ast.type;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.JassType;

public interface JassTypeToken {
	JassType resolve(GlobalScope globalScope);
}
