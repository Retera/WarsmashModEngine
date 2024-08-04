package com.etheller.interpreter.ast.definition;

import java.util.List;

import com.etheller.interpreter.ast.util.JassProgram;

public class JassScopeDefinitionBlock implements JassDefinitionBlock {
	private final String scopeName;
	private final List<JassDefinitionBlock> blocks;

	public JassScopeDefinitionBlock(final String scopeName, final List<JassDefinitionBlock> blocks) {
		this.scopeName = scopeName;
		this.blocks = blocks;
	}

	public List<JassDefinitionBlock> getBlocks() {
		return this.blocks;
	}

	@Override
	public void define(final String mangledNameScope, final JassProgram jassProgram) {
		final String name = mangledNameScope + this.scopeName;
		final String namePrefix = name + name.hashCode() + "__";
		for (final JassDefinitionBlock block : this.blocks) {
			block.define(namePrefix, jassProgram);
		}
	}

}
