package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

/**
 * Not a native
 *
 * @author Eric
 *
 */
public final class UserJassFunction extends AbstractJassFunction {
	private final List<JassStatement> statements;

	public UserJassFunction(final List<JassStatement> statements, final List<JassParameter> parameters,
			final JassType returnType) {
		super(parameters, returnType);
		this.statements = statements;
	}

	@Override
	public JassValue innerCall(final List<JassValue> arguments, final GlobalScope globalScope,
			final TriggerExecutionScope triggerScope, final LocalScope localScope) {
		for (final JassStatement statement : this.statements) {
			final JassValue returnValue = statement.execute(globalScope, localScope, triggerScope);
			if (returnValue != null) {
				if (!this.returnType.isAssignableFrom(returnValue.visit(JassTypeGettingValueVisitor.getInstance()))) {
					if ((this.returnType == JassType.NOTHING)
							&& (returnValue == JassReturnNothingStatement.RETURN_NOTHING_NOTICE)) {
						return null;
					}
					else if ((this.returnType.isNullable())
							&& (returnValue == JassReturnNothingStatement.RETURN_NOTHING_NOTICE)) {
						return this.returnType.getNullValue();
					}
					else {
						throw new JassException(globalScope, "Invalid return type", null);
					}
				}
				return returnValue;
			}
		}
		if (JassType.NOTHING != this.returnType) {
			throw new JassException(globalScope, "Invalid return type", null);
		}
		return null;
	}
}
