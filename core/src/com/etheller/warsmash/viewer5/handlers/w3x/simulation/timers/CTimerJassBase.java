package com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers;

import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.value.CodeJassValue;

public abstract class CTimerJassBase extends CTimer {
	public abstract void addEvent(final Trigger trigger);

	public abstract void removeEvent(final Trigger trigger);

	public abstract void setHandlerFunc(final CodeJassValue handlerFunc);

}
