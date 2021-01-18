package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.WarsmashConstants;

public class ParticleEmitter2 extends MdxEmitter<MdxComplexInstance, ParticleEmitter2Object, Particle2> {
	private static final float[] emissionRateHeap = new float[1];

	protected final MdxNode node;
	private int lastEmissionKey;

	public ParticleEmitter2(final MdxComplexInstance instance, final ParticleEmitter2Object emitterObject) {
		super(instance, emitterObject);

		this.node = instance.nodes[emitterObject.index];
		this.lastEmissionKey = -1;
	}

	@Override
	protected void updateEmission(final float dt) {
		final MdxComplexInstance instance = this.instance;

		if (instance.allowParticleSpawn) {
			final ParticleEmitter2Object emitterObject = this.emitterObject;
			final int keyframe = emitterObject.getEmissionRate(emissionRateHeap, instance.sequence, instance.frame,
					instance.counter);

			if (emitterObject.squirt != 0) {
				if (keyframe != this.lastEmissionKey) {
					this.currentEmission += emissionRateHeap[0];
				}

				this.lastEmissionKey = keyframe;
			}
			else {
				this.currentEmission += emissionRateHeap[0] * dt * WarsmashConstants.MODEL_DETAIL_PARTICLE_FACTOR;
			}
		}
	}

	@Override
	protected void emit() {
		if (this.emitterObject.head) {
			this.emitObject(0);
		}

		if (this.emitterObject.tail) {
			this.emitObject(1);
		}

	}

	@Override
	protected Particle2 createObject() {
		return new Particle2(this);
	}

}
