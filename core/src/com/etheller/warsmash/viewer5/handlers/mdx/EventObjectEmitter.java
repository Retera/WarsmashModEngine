package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.EmittedObject;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public abstract class EventObjectEmitter<EMITTER_OBJECT extends EventObjectEmitterObject, EMITTED_OBJECT extends EmittedObject<MdxComplexInstance, ? extends MdxEmitter<MdxComplexInstance, EMITTER_OBJECT, EMITTED_OBJECT>>>
		extends MdxEmitter<MdxComplexInstance, EMITTER_OBJECT, EMITTED_OBJECT> {
	private final int number = 0;

	public EventObjectEmitter(final MdxComplexInstance instance, final EMITTER_OBJECT emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected void updateEmission(final float dt) {
		final MdxComplexInstance instance = this.instance;

		if (instance.allowParticleSpawn) {
			final EMITTER_OBJECT emitterObject = this.emitterObject;

			emitterObject.getV
		}

	}

	@Override
	protected void emit() {
		this.emitObject(0);
	}

}
