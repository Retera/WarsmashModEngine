package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

public class NativeJassFunction {
	private final List<JassParameter> parameters;
	private final JassType returnType;
	private final String name;
	private final JassFunction implementation;

	public NativeJassFunction(final List<JassParameter> parameters, final JassType returnType, final String name,
			final JassFunction impl) {
		this.parameters = parameters;
		this.returnType = returnType;
		this.name = name;
		this.implementation = impl;
	}

	public List<JassParameter> getParameters() {
		return this.parameters;
	}

	public JassType getReturnType() {
		return this.returnType;
	}

	public String getName() {
		return this.name;
	}

	public JassFunction getImplementation() {
		return this.implementation;
	}

	public final JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope) {
		if (arguments.size() != this.parameters.size()) {
			throw new JassException(globalScope, "Invalid number of arguments passed to function: " + arguments.size()
					+ " != " + this.parameters.size(), null);
		}
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
		}
		if (!checkNativeExists()) {
			return this.returnType.getNullValue();
		}
		return this.implementation.call(arguments, globalScope, triggerScope);
	}

	private boolean checkNativeExists() {
		if (this.implementation == null) {
			System.err.println(
					"Call to native function that was declared but had no native implementation: " + this.name);
			return false;
//			throw new UnsupportedOperationException(
//					"Call to native function that was declared but had no native implementation: " + this.name);
		}
		return true;
	}
}
