package com.etheller.interpreter.ast.debug;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;

public class JassException extends RuntimeException {

	public JassException(final GlobalScope globalScope, final String message, final Exception javaCause) {
		super(message(globalScope, message), javaCause);
	}

	private static String message(final GlobalScope globalScope, final String message) {
		final List<JassStackElement> stackTrace = globalScope.copyJassStack();
		final StringBuilder sb = new StringBuilder(message);
		for (final JassStackElement element : stackTrace) {
			sb.append("\n");
			sb.append(element);
		}
		return sb.toString();
	}
}
