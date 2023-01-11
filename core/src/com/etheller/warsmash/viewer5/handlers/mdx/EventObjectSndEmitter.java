package com.etheller.warsmash.viewer5.handlers.mdx;

public class EventObjectSndEmitter extends EventObjectEmitter<EventObjectEmitterObject, EventObjectSnd> {

	public EventObjectSndEmitter(final MdxComplexInstance instance, final EventObjectEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected EventObjectSnd createObject() {
		return new EventObjectSnd(this);
	}

}
