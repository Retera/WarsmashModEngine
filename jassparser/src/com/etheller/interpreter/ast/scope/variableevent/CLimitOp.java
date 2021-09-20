package com.etheller.interpreter.ast.scope.variableevent;

import com.etheller.interpreter.ast.util.CHandle;

public enum CLimitOp implements CHandle {
	LESS_THAN,
	LESS_THAN_OR_EQUAL,
	EQUAL,
	GREATER_THAN_OR_EQUAL,
	GREATER_THAN,
	NOT_EQUAL;

	public static CLimitOp[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
