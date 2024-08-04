package com.etheller.interpreter.ast.type;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.value.JassType;

public class NothingJassTypeToken implements JassTypeToken {
	public static final NothingJassTypeToken INSTANCE = new NothingJassTypeToken();

	@Override
	public JassType resolve(final GlobalScope globalScope) {
		return JassType.NOTHING;
	}

}
