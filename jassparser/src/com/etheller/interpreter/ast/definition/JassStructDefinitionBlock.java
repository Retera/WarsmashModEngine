package com.etheller.interpreter.ast.definition;

import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.value.StructJassType;

public class JassStructDefinitionBlock implements JassDefinitionBlock {
	private final StructJassType structJassType;

	public JassStructDefinitionBlock(final StructJassType structJassType) {
		this.structJassType = structJassType;
	}

	@Override
	public void define(final JassProgram jassProgram) {
		jassProgram.globalScope.defineStruct(this.structJassType);
	}

}
