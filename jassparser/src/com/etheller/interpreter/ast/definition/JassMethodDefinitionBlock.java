package com.etheller.interpreter.ast.definition;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.interpreter.ast.debug.DebuggingJassStatement;
import com.etheller.interpreter.ast.expression.JassExpression;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.qualifier.JassQualifier;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.statement.JassReturnStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.statement.JassThrowStatement;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.StructJassType;

public class JassMethodDefinitionBlock extends JassCodeDefinitionBlock {
	public JassMethodDefinitionBlock(final int lineNo, final String sourceFile, final EnumSet<JassQualifier> qualifiers,
			final String name, final List<JassStatement> statements,
			final List<JassParameterDefinition> parameterDefinitions, final JassTypeToken returnType) {
		super(lineNo, sourceFile, qualifiers, name, statements, parameterDefinitions, returnType);
	}

	public static JassMethodDefinitionBlock createInterfaceMethod(final int lineNo, final String sourceFile,
			final EnumSet<JassQualifier> qualifiers, final String name,
			final List<JassParameterDefinition> parameterDefinitions, final JassTypeToken returnType,
			final JassExpression defaultsExpression) {
		final List<JassStatement> statements = new ArrayList<>();
		if (defaultsExpression != null) {
			final JassReturnStatement returnStatement = new JassReturnStatement(defaultsExpression);
			if (JassSettings.DEBUG) {
				statements.add(new DebuggingJassStatement(lineNo, returnStatement));
			}
			else {
				statements.add(returnStatement);
			}
		}
		else {
//			statements.add(new JassReturnStatement(new LiteralJassExpression(JassType.NOTHING.getNullValue())));
			final JassThrowStatement throwStatement = new JassThrowStatement(lineNo, sourceFile,
					"Called an interface method that was not defined in the derived type: " + name);
			if (JassSettings.DEBUG) {
				statements.add(new DebuggingJassStatement(lineNo, throwStatement));
			}
			else {
				statements.add(throwStatement);
			}
		}
		return new JassMethodDefinitionBlock(lineNo, sourceFile, qualifiers, name, statements, parameterDefinitions,
				returnType);
	}

	public UserJassFunction createCode(final Scope globalScope, final StructJassType structType) {
		final List<JassParameter> resolvedUserParameters = JassParameterDefinition.resolve(getParameterDefinitions(),
				globalScope);
		final List<JassParameter> parameters;
		if (getQualifiers().contains(JassQualifier.STATIC)) {
			parameters = resolvedUserParameters;
		}
		else {
			parameters = new ArrayList<>();
			parameters.add(new JassParameter(structType, GlobalScope.KEYNAME_THIS));
			parameters.addAll(resolvedUserParameters);
		}
		return new UserJassFunction(getQualifiers(), getStatements(), parameters, getReturnType().resolve(globalScope));
	}

}
