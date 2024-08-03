package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.util.JassProgram;

public class JassTypeDefinitionBlock implements JassDefinitionBlock {
	private final String id;
	private final String supertype;

	public JassTypeDefinitionBlock(final String id, final String supertype) {
		this.id = id;
		this.supertype = supertype;
	}

	@Override
	public void define(final JassProgram jassProgram) {
		jassProgram.globalScope.loadTypeDefinition(this.id, this.supertype);
	}

}
