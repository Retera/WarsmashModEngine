package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.statement.JassReturnNothingStatement;
import com.etheller.interpreter.ast.statement.JassStatement;
import com.etheller.interpreter.ast.value.DummyJassValue;
import com.etheller.interpreter.ast.value.JassType;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.JassTypeGettingValueVisitor;

/**
 * Not a native
 *
 * @author Retera
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
				return returnByValue(globalScope, returnValue);
			}
		}
		if (JassType.NOTHING != this.returnType) {
			throw new JassException(globalScope, "Invalid return type", null);
		}
		return null;
	}

	@Override
	public JassValue continueExecuting(final JassStack stack) {
		while (stack.step < this.statements.size()) {
			final JassStatement statement = this.statements.get(stack.step);
			final JassValue returnValue = statement.continueExecuting(stack);
			if (returnValue != null) {
				if (returnValue == DummyJassValue.PAUSE_FOR_SLEEP) {
					return returnValue;
				}
				return returnByValue(stack.globalScope, returnValue);
			}
			stack.step++;
		}
		if (JassType.NOTHING != this.returnType) {
			throw new JassException(stack.globalScope, "Invalid return type", null);
		}
		return null;
	}

	private JassValue returnByValue(final GlobalScope globalScope, final JassValue returnValue) {
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

	public class UserJassFunctionStack extends JassStack {
		private final int step;

		public UserJassFunctionStack(final GlobalScope globalScope, final LocalScope localScope,
				final TriggerExecutionScope triggerScope) {
			super(globalScope, localScope, triggerScope);
			this.step = 0;
		}

		@Override
		public void update() {
			while (this.step < UserJassFunction.this.statements.size()) {
				final JassStatement statement = UserJassFunction.this.statements.get(this.step);
				final JassValue returnValue = statement.continueExecuting(this);
				if (returnValue != null) {
					if (returnValue == DummyJassValue.PAUSE_FOR_SLEEP) {
						return returnValue;
					}
					return returnByValue(this.globalScope, returnValue);
				}
				this.step++;
			}
			if (JassType.NOTHING != UserJassFunction.this.returnType) {
				throw new JassException(this.globalScope, "Invalid return type", null);
			}
			return null;
		}

	}

	public List<JassStatement> getStatements() {
		return this.statements;
	}
}
