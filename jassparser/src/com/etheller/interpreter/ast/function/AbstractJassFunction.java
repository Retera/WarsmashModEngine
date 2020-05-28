package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;

/**
 * Not a native
 *
 * @author Eric
 *
 */
public abstract class AbstractJassFunction implements JassFunction {
	protected final List<JassParameter> parameters;
	protected final JassType returnType;

	public AbstractJassFunction(final List<JassParameter> parameters, final JassType returnType) {
		this.parameters = parameters;
		this.returnType = returnType;
	}

	@Override
	public final JassValue call(final List<JassValue> arguments, final GlobalScope globalScope) {
		if (arguments.size() != parameters.size()) {
			throw new RuntimeException("Invalid number of arguments passed to function");
		}
		final LocalScope localScope = new LocalScope();
		for (int i = 0; i < parameters.size(); i++) {
			final JassParameter parameter = parameters.get(i);
			final JassValue argument = arguments.get(i);
			if (!parameter.matchesType(argument)) {
				throw new RuntimeException("Invalid type for specified argument");
			}
			localScope.createLocal(parameter.getIdentifier(), parameter.getType(), argument);
		}
		return innerCall(arguments, globalScope, localScope);
	}

	protected abstract JassValue innerCall(final List<JassValue> arguments, final GlobalScope globalScope,
			final LocalScope localScope);
}
