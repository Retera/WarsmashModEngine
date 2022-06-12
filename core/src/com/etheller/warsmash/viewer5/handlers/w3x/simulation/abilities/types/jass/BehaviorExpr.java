package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public interface BehaviorExpr {
	CBehavior evaluate(GlobalScope globalScope, TriggerExecutionScope triggerScope);
}
