package com.etheller.interpreter.ast.definition;

import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.interpreter.ast.util.JassSettings;

public class JassFunctionDefinitionBlock extends JassCodeDefinitionBlock implements JassDefinitionBlock {

	public JassFunctionDefinitionBlock(final int lineNo, final String sourceFile,
			final EnumSet<JassQualifier> qualifiers, final String name, final List<JassStatement> statements,
			final List<JassParameterDefinition> parameterDefinitions, final JassTypeToken returnType) {
		super(lineNo, sourceFile, qualifiers, name, statements, parameterDefinitions, returnType);
	}

	@Override
	public void define(final Scope scope, final JassProgram jassProgram) {
		scope.defineFunction(getLineNo(), getSourceFile(), getName(), createCode(scope), scope);
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Defining jass user function: " + this.getName());
		}
	}

	private UserJassFunction createCode(final Scope globalScope) {
		return new UserJassFunction(getQualifiers(), getStatements(),
				JassParameterDefinition.resolve(getParameterDefinitions(), globalScope),
				getReturnType().resolve(globalScope));
	}
}
