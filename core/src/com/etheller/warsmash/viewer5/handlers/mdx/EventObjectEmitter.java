package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.EmittedObject;

public abstract class EventObjectEmitter<EMITTER_OBJECT extends EventObjectEmitterObject, EMITTED_OBJECT extends EmittedObject<MdxComplexInstance, ? extends MdxEmitter<MdxComplexInstance, EMITTER_OBJECT, EMITTED_OBJECT>>>
		extends MdxEmitter<MdxComplexInstance, EMITTER_OBJECT, EMITTED_OBJECT> {
	private static final long[] valueHeap = { 0L };

	private long lastValue = 0;

	public EventObjectEmitter(final MdxComplexInstance instance, final EMITTER_OBJECT emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected void updateEmission(final float dt) {
		final MdxComplexInstance instance = this.instance;

		if (instance.allowParticleSpawn) {
			final EMITTER_OBJECT emitterObject = this.emitterObject;

			emitterObject.getValue(valueHeap, instance);

			final long value = valueHeap[0];

			if ((value == 1) && (value != this.lastValue)) {
				this.currentEmission += 1;
			}

			this.lastValue = value;
		}

	}

	@Override
	protected void emit() {
		this.emitObject(0);
	}

}
