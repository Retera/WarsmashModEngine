package com.etheller.interpreter.ast.scope;

public class TypeDefinition {
	private final String name;
	private final String supertype;

	public TypeDefinition(final String name, final String supertype) {
		this.name = name;
		this.supertype = supertype;
	}
}
