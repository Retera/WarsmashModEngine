package com.etheller.interpreter.ast.type;

import java.util.ArrayList;
import java.util.List;

import com.etheller.interpreter.ast.definition.JassParameterDefinition;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.scope.Scope;
import com.etheller.interpreter.ast.value.JassType;

public class LiteralJassTypeToken implements JassTypeToken {
	private final JassType jassType;

	public LiteralJassTypeToken(final JassType jassType) {
		this.jassType = jassType;
	}

	@Override
	public JassType resolve(final Scope globalScope) {
		return this.jassType;
	}

	public static List<JassParameterDefinition> unresolve(final List<JassParameter> parameters) {
		final List<JassParameterDefinition> parameterDefintions = new ArrayList<>();
		for (final JassParameter parameter : parameters) {
			parameterDefintions.add(new JassParameterDefinition(new LiteralJassTypeToken(parameter.getType()),
					parameter.getIdentifier()));
		}
		return parameterDefintions;
	}
}
