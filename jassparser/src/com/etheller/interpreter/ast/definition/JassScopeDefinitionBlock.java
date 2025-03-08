package com.etheller.interpreter.ast.definition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassCallStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.util.JassProgram;

public class JassScopeDefinitionBlock implements JassDefinitionBlock {
	private final int lineNo;
	private final String sourceFile;
	private final String scopeName;
	private final List<JassDefinitionBlock> blocks;
	private final String initializerName;

	public JassScopeDefinitionBlock(final int lineNo, final String sourceFile, final String scopeName,
			final List<JassDefinitionBlock> blocks, final String initializerName) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.scopeName = scopeName;
		this.blocks = blocks;
		this.initializerName = initializerName;
	}

	public List<JassDefinitionBlock> getBlocks() {
		return this.blocks;
	}

	public String getInitializerName() {
		return this.initializerName;
	}

	@Override
	public void define(final Scope scope, final JassProgram jassProgram) {
		final Scope childScope = scope.createNestedScope(this.scopeName, false);
		for (final JassDefinitionBlock block : this.blocks) {
			block.define(childScope, jassProgram);
		}
		if (this.initializerName != null) {
			childScope.defineGlobals(this.lineNo, this.sourceFile,
					Arrays.<JassStatement>asList(new JassCallStatement(this.initializerName, Collections.emptyList())),
					childScope);
		}
	}

}
