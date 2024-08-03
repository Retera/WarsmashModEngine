package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.value.JassType;

public class JassGlobalDefinitionStatement implements JassStatement {
	private final String identifier;
	private final JassExpression expression;
	private final JassType type;

	public JassGlobalDefinitionStatement(final String identifier, final JassType type,
			final JassExpression expression) {
		this.identifier = identifier;
		this.type = type;
		this.expression = expression;
	}

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassExpression getExpression() {
		return this.expression;
	}

	public JassType getType() {
		return this.type;
	}

}
