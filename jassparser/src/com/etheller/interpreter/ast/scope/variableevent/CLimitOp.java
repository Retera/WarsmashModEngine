package com.etheller.interpreter.ast.scope.variableevent;

public enum CLimitOp {
	LESS_THAN,
	LESS_THAN_OR_EQUAL,
	EQUAL,
	GREATER_THAN_OR_EQUAL,
	GREATER_THAN,
	NOT_EQUAL;

	public static CLimitOp[] VALUES = values();
}
