package com.etheller.interpreter.ast.definition;

import java.util.List;

import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.util.JassSettings;

public class JassFunctionDefinitionBlock extends JassCodeDefinitionBlock implements JassDefinitionBlock {

	public JassFunctionDefinitionBlock(final int lineNo, final String sourceFile, final String name,
			final List<JassStatement> statements, final List<JassParameterDefinition> parameterDefinitions,
			final JassTypeToken returnType) {
		super(lineNo, sourceFile, name, statements, parameterDefinitions, returnType);
	}

	@Override
	public void define(final String mangledNameScope, final JassProgram jassProgram) {
		jassProgram.globalScope.defineFunction(getLineNo(), getSourceFile(), getName(),
				createCode(jassProgram.getGlobalScope()), mangledNameScope);
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Defining jass user function: " + this.getName());
		}
	}

	private UserJassFunction createCode(final GlobalScope globalScope) {
		return new UserJassFunction(getStatements(),
				JassParameterDefinition.resolve(getParameterDefinitions(), globalScope),
				getReturnType().resolve(globalScope));
	}
}
