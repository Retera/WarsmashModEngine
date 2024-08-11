package com.etheller.interpreter.ast.type;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.value.JassType;

public class PrimitiveJassTypeToken implements JassTypeToken {
	private final String id;

	public PrimitiveJassTypeToken(final String id) {
		this.id = id;
	}

	@Override
	public JassType resolve(final Scope globalScope) {
		return globalScope.parseType(this.id);
	}

}
