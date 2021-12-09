package com.etheller.warsmash.parsers.jass.triggers;

import java.util.Collections;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class BoolExprCondition implements TriggerBooleanExpression {
	private final JassFunction takesNothingReturnsBooleanFunction;

	public BoolExprCondition(final JassFunction returnsBooleanFunction) {
		this.takesNothingReturnsBooleanFunction = returnsBooleanFunction;
	}

	@Override
	public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		final JassValue booleanJassReturnValue;
		try {
			booleanJassReturnValue = this.takesNothingReturnsBooleanFunction.call(Collections.EMPTY_LIST, globalScope,
					triggerScope);
		}
		catch (final Exception e) {
			throw new JassException(globalScope, "Exception during BoolExprCondition.evaluate()", e);
		}
		if ((booleanJassReturnValue == null) && JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
			return false;
		}
		final Boolean booleanReturnValue = booleanJassReturnValue.visit(BooleanJassValueVisitor.getInstance());
		return booleanReturnValue.booleanValue();
	}

}
