package com.etheller.interpreter.ast.definition;

import java.util.List;

import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.JassType;

public class JassLibraryDefinitionBlock implements JassDefinitionBlock {
	private final String libraryName;
	private final List<JassLibraryRequirementDefinition> requirements;
	private final List<JassDefinitionBlock> blocks;

	public JassLibraryDefinitionBlock(final String libraryName,
			final List<JassLibraryRequirementDefinition> requirements, final List<JassDefinitionBlock> blocks) {
		this.libraryName = libraryName;
		this.requirements = requirements;
		this.blocks = blocks;
	}

	public String getLibraryName() {
		return this.libraryName;
	}

	public List<JassLibraryRequirementDefinition> getRequirements() {
		return this.requirements;
	}

	public List<JassDefinitionBlock> getBlocks() {
		return this.blocks;
	}

	@Override
	public void define(final String mangledNameScope, final JassProgram jassProgram) {
		final String name = mangledNameScope + this.libraryName;
		final String namePrefix = name + "__";
		jassProgram.globalScope.createGlobal(name, JassType.BOOLEAN, BooleanJassValue.TRUE);
		for (final JassDefinitionBlock block : this.blocks) {
			block.define(namePrefix, jassProgram);
		}
	}

}
