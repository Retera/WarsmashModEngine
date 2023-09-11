package com.etheller.warsmash.viewer5.handlers.mdx;

public class EventObjectUbrEmitter extends EventObjectEmitter<EventObjectEmitterObject, EventObjectUbr> {
	public EventObjectUbrEmitter(final MdxComplexInstance instance, final EventObjectEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected EventObjectUbr createObject() {
		return new EventObjectUbr(this);
	}

}
