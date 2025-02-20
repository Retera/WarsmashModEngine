package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.value.StringJassValue;

public class JassExitWhenStatement implements JassStatement {
	public static final StringJassValue LOOP_EXIT_NOTICE = new StringJassValue("EXIT");
	private final JassExpression expression;

	public JassExitWhenStatement(final JassExpression expression) {
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public JassExpression getExpression() {
		return this.expression;
	}

}
