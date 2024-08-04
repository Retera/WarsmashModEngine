package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.value.BooleanJassValue;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.BooleanJassValueVisitor;

public class BoolExprFilter implements TriggerBooleanExpression {
	private final CodeJassValue takesNothingReturnsBooleanFunction;

	public BoolExprFilter(final CodeJassValue returnsBooleanFunction) {
		this.takesNothingReturnsBooleanFunction = returnsBooleanFunction;
	}

	@Override
	public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		final JassValue jassReturnValue = this.takesNothingReturnsBooleanFunction.callAndExecuteCapturingReturnValue(
				globalScope, triggerScope, "BoolExprFilter", BooleanJassValue.FALSE);
		final Boolean booleanReturnValue = jassReturnValue.visit(BooleanJassValueVisitor.getInstance());
		return booleanReturnValue.booleanValue();
	}

}
