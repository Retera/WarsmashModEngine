package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.EmittedObject;

public abstract class EventObjectEmitter<EMITTER_OBJECT extends EventObjectEmitterObject, EMITTED_OBJECT extends EmittedObject<MdxComplexInstance, ? extends MdxEmitter<MdxComplexInstance, EMITTER_OBJECT, EMITTED_OBJECT>>>
		extends MdxEmitter<MdxComplexInstance, EMITTER_OBJECT, EMITTED_OBJECT> {
	private static final long[] valueHeap = { 0L };

	private int lastEmissionKey;

	public EventObjectEmitter(final MdxComplexInstance instance, final EMITTER_OBJECT emitterObject) {
		super(instance, emitterObject);
		this.lastEmissionKey = -1;
	}

	@Override
	protected void updateEmission(final float dt) {
		final MdxComplexInstance instance = this.instance;

		if (instance.allowParticleSpawn) {
			final EMITTER_OBJECT emitterObject = this.emitterObject;

			final int keyframe = emitterObject.getValue(valueHeap, instance);

			if (keyframe != this.lastEmissionKey) {
				this.currentEmission += 1;
				this.lastEmissionKey = keyframe;
			}
		}

	}

	public void reset() {
		this.lastEmissionKey = -1;
	}

	@Override
	protected void emit() {
		this.emitObject(0);
	}

}
