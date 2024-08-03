package com.etheller.interpreter.ast.type;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.JassType;

public class ArrayJassTypeToken implements JassTypeToken {
	private final String id;

	public ArrayJassTypeToken(final String id) {
		this.id = id;
	}

	@Override
	public JassType resolve(final GlobalScope globalScope) {
		return globalScope.parseType(this.id);
	}
}
