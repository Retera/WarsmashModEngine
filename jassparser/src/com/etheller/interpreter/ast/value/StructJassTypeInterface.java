package com.etheller.interpreter.ast.value;

import com.etheller.interpreter.ast.struct.JassStructMemberType;

public interface StructJassTypeInterface {
	JassStructMemberType tryGetMemberByName(final String name);

	JassStructMemberType getMemberByName(final String name);

	int getMemberIndexInefficientlyByName(final String name);

	int tryGetMemberIndexInefficientlyByName(final String name);
}
