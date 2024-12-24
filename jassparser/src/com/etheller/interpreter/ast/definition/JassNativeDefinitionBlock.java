package com.etheller.interpreter.ast.definition;

import java.util.List;

import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.util.JassSettings;

public class JassNativeDefinitionBlock implements JassDefinitionBlock {
	private final int lineNo;
	private final String name;
	private final String currentParsingFilePath;
	private final List<JassParameterDefinition> parameters;
	private final JassTypeToken returnType;

	public JassNativeDefinitionBlock(final int lineNo, final String currentParsingFilePath, final String name,
			final List<JassParameterDefinition> parameters, final JassTypeToken returnType) {
		this.lineNo = lineNo;
		this.name = name;
		this.currentParsingFilePath = currentParsingFilePath;
		this.parameters = parameters;
		this.returnType = returnType;
	}

	@Override
	public void define(final Scope scope, final JassProgram jassProgram) {
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Registering native: " + this.name);
		}
		jassProgram.jassNativeManager.registerNativeCode(this.lineNo, this.currentParsingFilePath, this.name,
				this.parameters, this.returnType, scope);
	}

}
