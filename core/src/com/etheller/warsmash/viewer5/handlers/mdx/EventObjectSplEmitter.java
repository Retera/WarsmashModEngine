package com.etheller.warsmash.viewer5.handlers.mdx;

public class EventObjectSplEmitter extends EventObjectEmitter<EventObjectEmitterObject, EventObjectSpl> {
	public EventObjectSplEmitter(final MdxComplexInstance instance, final EventObjectEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected EventObjectSpl createObject() {
		return new EventObjectSpl(this);
	}

}
