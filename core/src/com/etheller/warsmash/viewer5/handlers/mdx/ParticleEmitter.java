package com.etheller.warsmash.viewer5.handlers.mdx;

public class ParticleEmitter extends MdxEmitter<MdxComplexInstance, ParticleEmitterObject, Particle> {

	private static final float[] emissionRateHeap = new float[1];

	public ParticleEmitter(final MdxComplexInstance instance, final ParticleEmitterObject emitterObject) {
		super(instance, emitterObject);
	}

	@Override
	protected void updateEmission(final float dt) {
		final MdxComplexInstance instance = this.instance;

		if (instance.allowParticleSpawn) {
			final ParticleEmitterObject emitterObject = this.emitterObject;

			emitterObject.getEmissionRate(emissionRateHeap, instance.sequence, instance.frame, instance.counter);

			this.currentEmission += emissionRateHeap[0] * dt;
		}
	}

	@Override
	public void kill(final Particle object) {
		super.kill(object);
		object.onRemove();
	}

	@Override
	protected void emit() {
		emitObject(0);
	}

	@Override
	protected Particle createObject() {
		return new Particle(this);
	}

	public void onRemove() {
		for (final Particle particle : this.objects) {
			particle.onRemove();
		}
	}

}
