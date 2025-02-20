package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.CodeJassValue;
import com.etheller.interpreter.ast.value.JassValue;
import com.etheller.interpreter.ast.value.visitor.ObjectJassValueVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class CodeJassValueBehaviorExpr implements BehaviorExpr {
	private final CodeJassValue function;

	public CodeJassValueBehaviorExpr(final CodeJassValue function) {
		this.function = function;
	}

	@Override
	public CBehavior evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		final JassValue jassReturnValue = this.function.callAndExecuteCapturingReturnValue(globalScope,
				TriggerExecutionScope.EMPTY /* TODO this.jassAbility.getJassAbilityBasicScope() */,
				"CodeJassValueBehaviorExpr", null);
		if (jassReturnValue == null) {
			return null;
		}
		return jassReturnValue.visit(ObjectJassValueVisitor.getInstance());
	}

}
