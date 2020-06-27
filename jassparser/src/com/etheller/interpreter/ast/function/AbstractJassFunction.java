package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

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
		if (arguments.size() != this.parameters.size()) {
			throw new RuntimeException("Invalid number of arguments passed to function");
		}
		final LocalScope localScope = new LocalScope();
		for (int i = 0; i < this.parameters.size(); i++) {
			final JassParameter parameter = this.parameters.get(i);
			final JassValue argument = arguments.get(i);
			if (!parameter.matchesType(argument)) {
				System.err.println(
						parameter.getType() + " != " + argument.visit(JassTypeGettingValueVisitor.getInstance()));
				throw new RuntimeException(
						"Invalid type " + argument.visit(JassTypeGettingValueVisitor.getInstance()).getName()
								+ " for specified argument " + parameter.getType().getName());
			}
			localScope.createLocal(parameter.getIdentifier(), parameter.getType(), argument);
		}
		return innerCall(arguments, globalScope, localScope);
	}

	protected abstract JassValue innerCall(final List<JassValue> arguments, final GlobalScope globalScope,
			final LocalScope localScope);
}
