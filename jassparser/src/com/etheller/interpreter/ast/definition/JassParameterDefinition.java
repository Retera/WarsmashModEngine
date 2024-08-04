package com.etheller.interpreter.ast.definition;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.type.JassTypeToken;

public class JassParameterDefinition {
	private final JassTypeToken typeToken;
	private final String identifier;

	public JassParameterDefinition(final JassTypeToken typeToken, final String identifier) {
		this.typeToken = typeToken;
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public JassTypeToken getTypeToken() {
		return this.typeToken;
	}

	public JassParameter resolve(final GlobalScope globalScope) {
		return new JassParameter(this.typeToken.resolve(globalScope), this.identifier);
	}

	public static List<JassParameter> resolve(final List<JassParameterDefinition> parameterDefinitions,
			final GlobalScope globalScope) {
		final List<JassParameter> parameters = new ArrayList<>();
		for (final JassParameterDefinition parameterDefinition : parameterDefinitions) {
			parameters.add(parameterDefinition.resolve(globalScope));
		}
		return parameters;
	}
}
