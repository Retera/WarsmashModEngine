package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.type.JassTypeToken;
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
	protected final JassTypeToken returnType;

	public AbstractJassFunction(final List<JassParameter> parameters, final JassTypeToken returnType) {
		this.parameters = parameters;
		this.returnType = returnType;
	}

	@Override
	public final JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope) {
		if (arguments.size() != this.parameters.size()) {
			throw new JassException(globalScope, "Invalid number of arguments passed to function: " + arguments.size()
					+ " != " + this.parameters.size(), null);
		}
		final LocalScope localScope = new LocalScope();
		for (int i = 0; i < this.parameters.size(); i++) {
			final JassParameter parameter = this.parameters.get(i);
			final JassValue argument = arguments.get(i);
			if (!parameter.matchesType(argument)) {
				if ((parameter == null) || (argument == null)) {
					System.err.println(
							"We called some Jass function with incorrect argument types, and the types were null!!!");
					System.err.println("This is a temporary hack for tests and showcase programming solutions");
					return null;
				}
				System.err.println(
						parameter.getType() + " != " + argument.visit(JassTypeGettingValueVisitor.getInstance()));
				throw new JassException(globalScope,
						"Invalid type " + argument.visit(JassTypeGettingValueVisitor.getInstance()).getName()
								+ " for specified argument " + parameter.getType().getName(),
						null);
			}
			localScope.createLocal(parameter.getIdentifier(), parameter.getType(), argument);
		}
		return innerCall(arguments, globalScope, triggerScope, localScope);
	}

	protected abstract JassValue innerCall(final List<JassValue> arguments, final GlobalScope globalScope,
			TriggerExecutionScope triggerScope, final LocalScope localScope);

	public List<JassParameter> getParameters() {
		return this.parameters;
	}

	public JassTypeToken getReturnType() {
		return this.returnType;
	}
}
