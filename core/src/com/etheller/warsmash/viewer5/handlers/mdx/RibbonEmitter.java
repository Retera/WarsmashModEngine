package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.WarsmashConstants;

public class RibbonEmitter extends MdxEmitter<MdxComplexInstance, RibbonEmitterObject, Ribbon> {
	public Ribbon first;
	public Ribbon last;

	public RibbonEmitter(final MdxComplexInstance instance, final RibbonEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected void updateEmission(final float dt) {
		final MdxComplexInstance instance = this.instance;

		if (instance.allowParticleSpawn) {
			final RibbonEmitterObject emitterObject = this.emitterObject;

			// It doesn't make sense to emit more than 1 ribbon at the same time.
			this.currentEmission = Math.min(this.currentEmission
					+ (emitterObject.emissionRate * dt * WarsmashConstants.MODEL_DETAIL_PARTICLE_FACTOR), 1);
		}

	}

	@Override
	protected void emit() {
		final Ribbon ribbon = this.emitObject(0);
		final Ribbon last = this.last;

		if (last != null) {
			last.next = ribbon;
			ribbon.prev = last;
		}
		else {
			this.first = ribbon;
		}

		this.last = ribbon;
	}

	@Override
	public void kill(final Ribbon object) {
		super.kill(object);

		final Ribbon prev = object.prev;
		final Ribbon next = object.next;

		if (object == this.first) {
			this.first = next;
		}

		if (object == this.last) {
			this.first = null;
			this.last = null;
		}

		if (prev != null) {
			prev.next = next;
		}

		if (next != null) {
			next.prev = prev;
		}

		object.prev = null;
		object.next = null;
	}

	@Override
	protected Ribbon createObject() {
		return new Ribbon(this);
	}

}
