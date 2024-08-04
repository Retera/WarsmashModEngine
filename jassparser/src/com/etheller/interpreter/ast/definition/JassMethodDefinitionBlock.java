package com.etheller.interpreter.ast.definition;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.UserJassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.type.JassTypeToken;
import com.etheller.interpreter.ast.value.StructJassType;

public class JassMethodDefinitionBlock extends JassCodeDefinitionBlock {
	private final boolean staticMethod;

	public JassMethodDefinitionBlock(final int lineNo, final String sourceFile, final String name,
			final List<JassStatement> statements, final List<JassParameterDefinition> parameterDefinitions,
			final JassTypeToken returnType, final boolean staticMethod) {
		super(lineNo, sourceFile, name, statements, parameterDefinitions, returnType);
		this.staticMethod = staticMethod;
	}

	public UserJassFunction createCode(final GlobalScope globalScope, final StructJassType structType) {
		final List<JassParameter> resolvedUserParameters = JassParameterDefinition.resolve(getParameterDefinitions(),
				globalScope);
		final List<JassParameter> parameters;
		if (this.staticMethod) {
			parameters = resolvedUserParameters;
		}
		else {
			parameters = new ArrayList<>();
			parameters.add(new JassParameter(structType, GlobalScope.KEYNAME_THIS));
			parameters.addAll(resolvedUserParameters);
		}
		return new UserJassFunction(getStatements(), parameters, getReturnType().resolve(globalScope));
	}

	public boolean isStaticMethod() {
		return this.staticMethod;
	}
}
