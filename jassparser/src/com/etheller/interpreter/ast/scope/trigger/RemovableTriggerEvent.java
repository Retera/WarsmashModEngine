package com.etheller.interpreter.ast.scope.trigger;

public abstract class RemovableTriggerEvent {
	public RemovableTriggerEvent(final Trigger t) {
		t.addEvent(this);
	}

	public abstract void remove();

//	RemovableTriggerEvent DO_NOTHING = new RemovableTriggerEvent() {
//		@Override
//		public void remove() {
//		}
//	};

	public static RemovableTriggerEvent doNothing(final Trigger trigger) {
		return new RemovableTriggerEvent(trigger) {
			@Override
			public void remove() {
			}
		};
	}
}
