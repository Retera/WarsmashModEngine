package com.etheller.warsmash.viewer5.handlers.mdx;

public class EventObjectSplEmitter extends EventObjectEmitter<EventObjectEmitterObject, EventObjectSplUbr> {
	public EventObjectSplEmitter(final MdxComplexInstance instance, final EventObjectEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected EventObjectSplUbr createObject() {
		return new EventObjectSplUbr(this);
	}
}
