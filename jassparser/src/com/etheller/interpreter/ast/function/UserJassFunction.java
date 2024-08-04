package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.value.JassType;

/**
 * Not a native
 *
 * @author Retera
 *
 */
public final class UserJassFunction {
	private final List<JassStatement> statements;
	private final List<JassParameter> parameters;
	private final JassType returnType;

	public UserJassFunction(final List<JassStatement> statements, final List<JassParameter> parameters,
			final JassType returnType) {
		this.statements = statements;
		this.parameters = parameters;
		this.returnType = returnType;
	}

	public List<JassStatement> getStatements() {
		return this.statements;
	}

	public List<JassParameter> getParameters() {
		return this.parameters;
	}

	public JassType getReturnType() {
		return this.returnType;
	}
}
