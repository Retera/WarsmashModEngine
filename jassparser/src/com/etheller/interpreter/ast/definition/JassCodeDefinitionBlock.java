package com.etheller.interpreter.ast.definition;

import java.util.List;

import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassCodeDefinitionBlock {
	private final int lineNo;
	private final String sourceFile;
	private final String name;
	private final List<JassStatement> statements;
	private final List<JassParameterDefinition> parameterDefinitions;
	private final JassTypeToken returnType;

	public JassCodeDefinitionBlock(final int lineNo, final String sourceFile, final String name,
			final List<JassStatement> statements, final List<JassParameterDefinition> parameterDefinitions,
			final JassTypeToken returnType) {
		this.lineNo = lineNo;
		this.sourceFile = sourceFile;
		this.name = name;
		this.statements = statements;
		this.parameterDefinitions = parameterDefinitions;
		this.returnType = returnType;
	}

	public int getLineNo() {
		return this.lineNo;
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public String getName() {
		return this.name;
	}

	public List<JassStatement> getStatements() {
		return this.statements;
	}

	public List<JassParameterDefinition> getParameterDefinitions() {
		return this.parameterDefinitions;
	}

	public JassTypeToken getReturnType() {
		return this.returnType;
	}
}
