package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CRegionTriggerLeave {
	private final GlobalScope globalScope;
	private final Trigger trigger;
	private final TriggerBooleanExpression filter;

	public CRegionTriggerLeave(final GlobalScope globalScope, final Trigger trigger,
			final TriggerBooleanExpression filter) {
		this.globalScope = globalScope;
		this.trigger = trigger;
		this.filter = filter;
	}

	public void fire(final CUnit unit, final CRegion region) {
		final CommonTriggerExecutionScope eventScope = CommonTriggerExecutionScope.unitLeaveRegionScope(
				JassGameEventsWar3.EVENT_GAME_LEAVE_REGION, this.trigger, TriggerExecutionScope.EMPTY, unit, region);
		this.globalScope.queueTrigger(this.filter,
				CommonTriggerExecutionScope.filterScope(TriggerExecutionScope.EMPTY, unit), this.trigger, eventScope,
				eventScope);
	}

	public Trigger getTrigger() {
		return this.trigger;
	}
}
