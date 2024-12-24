package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.value.DummyJassValue;

public class JassReturnNothingStatement implements JassStatement {
	public static final DummyJassValue RETURN_NOTHING_NOTICE = new DummyJassValue();

	@Override
	public <T> T accept(final JassStatementVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
