package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.util.JassSettings;
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
		final JassThread thread = globalScope.createThreadCapturingReturnValue(this.takesNothingReturnsBooleanFunction,
				triggerScope);
		final JassValue booleanJassReturnValue;
		try {
			globalScope.runThreadUntilCompletion(thread);
			if (thread.instructionPtr == -1) {
				booleanJassReturnValue = thread.stackFrame.getLast(0);
			}
			else {
				if (!JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
					throw new IllegalStateException(
							"The BoolExpr created a thread that did not immediately return; did you call TriggerSleepAction in a Filter??");
				}
				else {
					return false;
				}
			}
		}
		catch (final Exception e) {
			throw new JassException(globalScope, "Exception during BoolExprFilter.evaluate()", e);
		}
		final Boolean booleanReturnValue = booleanJassReturnValue.visit(BooleanJassValueVisitor.getInstance());
		return booleanReturnValue.booleanValue();
	}

}
