package com.etheller.interpreter.ast.definition;

import java.util.List;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.util.JassProgram;

public class JassGlobalsDefinitionBlock implements JassDefinitionBlock {
	private final int lineNo;
	private final String file;
	private final List<JassStatement> globalStatements;

	public JassGlobalsDefinitionBlock(final int lineNo, final String file, final List<JassStatement> globalStatements) {
		this.lineNo = lineNo;
		this.file = file;
		this.globalStatements = globalStatements;
	}

	@Override
	public void define(final Scope scope, final JassProgram jassProgram) {
		scope.defineGlobals(this.lineNo, this.file, this.globalStatements, scope);
	}

}
