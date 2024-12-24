package com.etheller.interpreter.ast.type;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.value.JassType;

public class NothingJassTypeToken implements JassTypeToken {
	public static final NothingJassTypeToken INSTANCE = new NothingJassTypeToken();

	@Override
	public JassType resolve(final Scope globalScope) {
		return JassType.NOTHING;
	}

}
