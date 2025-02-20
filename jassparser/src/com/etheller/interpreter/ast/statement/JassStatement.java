package com.etheller.interpreter.ast.statement;

public interface JassStatement {

	<T> T accept(JassStatementVisitor<T> visitor);
}
