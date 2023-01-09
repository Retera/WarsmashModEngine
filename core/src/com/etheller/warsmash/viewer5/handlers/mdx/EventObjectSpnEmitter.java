package com.etheller.warsmash.viewer5.handlers.mdx;

public class EventObjectSpnEmitter extends EventObjectEmitter<EventObjectEmitterObject, EventObjectSpn> {

	public EventObjectSpnEmitter(final MdxComplexInstance instance, final EventObjectEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected EventObjectSpn createObject() {
		return new EventObjectSpn(this);
	}

}
