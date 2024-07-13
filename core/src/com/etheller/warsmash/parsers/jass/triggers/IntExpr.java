package com.etheller.warsmash.parsers.jass.triggers;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.execution.JassThread;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.TriggerIntegerExpression;
import com.etheller.interpreter.ast.util.JassSettings;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.IntegerJassValueVisitor;

public class IntExpr implements TriggerIntegerExpression {
	private final CodeJassValue takesNothingReturnsIntegerFunction;

	public IntExpr(CodeJassValue takesNothingReturnsIntegerFunction) {
		this.takesNothingReturnsIntegerFunction = takesNothingReturnsIntegerFunction;
	}

	@Override
	public int evaluate(GlobalScope globalScope, TriggerExecutionScope triggerScope) {
		final JassThread thread = globalScope.createThreadCapturingReturnValue(this.takesNothingReturnsIntegerFunction,
				triggerScope);
		final JassValue jassReturnValue;
		try {
			globalScope.runThreadUntilCompletion(thread);
			if (thread.instructionPtr == -1) {
				jassReturnValue = thread.stackFrame.getLast(0);
			}
			else {
				if (!JassSettings.CONTINUE_EXECUTING_ON_ERROR) {
					throw new IllegalStateException(
							"The BoolExpr created a thread that did not immediately return; did you call TriggerSleepAction in a Condition??");
				}
				else {
					return 0;
				}
			}
		}
		catch (final Exception e) {
			throw new JassException(globalScope, "Exception during BoolExprCondition.evaluate()", e);
		}
		final Integer integerReturnValue = jassReturnValue.visit(IntegerJassValueVisitor.getInstance());
		return integerReturnValue.intValue();
	}

}
