package com.etheller.interpreter.ast.scope.trigger;

public interface RemovableTriggerEvent {
	void remove();

	RemovableTriggerEvent DO_NOTHING = new RemovableTriggerEvent() {
		@Override
		public void remove() {
		}
	};
}
