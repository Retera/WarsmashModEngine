package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.EmittedObject;
import com.etheller.warsmash.viewer5.Emitter;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public abstract class MdxEmitter<MODEL_INSTANCE extends ModelInstance, EMITTER_OBJECT extends EmitterObject, EMITTED_OBJECT extends EmittedObject<MODEL_INSTANCE, ? extends Emitter<MODEL_INSTANCE, EMITTED_OBJECT>>>
		extends Emitter<MODEL_INSTANCE, EMITTED_OBJECT> {

	protected final EMITTER_OBJECT emitterObject;

	public MdxEmitter(final MODEL_INSTANCE instance, final EMITTER_OBJECT emitterObject) {
		super(instance);

		this.emitterObject = emitterObject;
	}

	@Override
	public void update(final float dt, final boolean objectVisible) {
		if (!objectVisible) {
			return;
		}
		if (this.emitterObject.ok()) {
			super.update(dt, objectVisible);
		}
	}
}
