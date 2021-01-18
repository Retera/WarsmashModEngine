package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.EmittedObject;
import com.etheller.warsmash.viewer5.Scene;

/**
 * A spawned model particle.
 */
public class Particle extends EmittedObject<MdxComplexInstance, ParticleEmitter> {
	private static final Quaternion rotationHeap = new Quaternion();
	private static final Quaternion rotationHeap2 = new Quaternion();
	private static final Vector3 velocityHeap = new Vector3();
	private static final float[] latitudeHeap = new float[1];
//	private static final float[] longitudeHeap = new float[1];
	private static final float[] lifeSpanHeap = new float[1];
	private static final float[] gravityHeap = new float[1];
	private static final float[] speedHeap = new float[1];
	private static final float[] tempVector = new float[3];

	private final MdxComplexInstance internalInstance;
	private final Vector3 velocity = new Vector3();
	private float gravity;

	public Particle(final ParticleEmitter emitter) {
		super(emitter);

		final ParticleEmitterObject emitterObject = emitter.emitterObject;

		this.internalInstance = (MdxComplexInstance) emitterObject.internalModel.addInstance();
	}

	@Override
	protected void bind(final int flags) {
		final ParticleEmitter emitter = this.emitter;
		final MdxComplexInstance instance = emitter.instance;
		final int sequence = instance.sequence;
		final int frame = instance.frame;
		final int counter = instance.counter;
		final Scene scene = instance.scene;
		final ParticleEmitterObject emitterObject = emitter.emitterObject;
		final MdxNode node = instance.nodes[emitterObject.index];
		final MdxComplexInstance internalInstance = this.internalInstance;
		final Vector3 scale = node.worldScale;
		final Vector3 velocity = this.velocity;

		emitterObject.getLatitude(latitudeHeap, sequence, frame, counter);
		// longitude?? commented in ghostwolf JS
		emitterObject.getLifeSpan(lifeSpanHeap, sequence, frame, counter);
		emitterObject.getGravity(gravityHeap, sequence, frame, counter);
		emitterObject.getSpeed(speedHeap, sequence, frame, counter);

		this.health = lifeSpanHeap[0];
		this.gravity = gravityHeap[0] * scale.z;

		// Local rotation
		rotationHeap.idt();
		rotationHeap.mul(rotationHeap2.setFromAxisRad(0, 0, 1,
				RenderMathUtils.randomInRange((float) -Math.PI, (float) Math.PI)));
		rotationHeap.mul(rotationHeap2.setFromAxisRad(0, 1, 0,
				RenderMathUtils.randomInRange(-latitudeHeap[0], latitudeHeap[0])));
		velocity.set(RenderMathUtils.VEC3_UNIT_Z);
		rotationHeap.transform(velocity);

		// World rotation
		node.worldRotation.transform(velocity);

		// Apply speed
		velocity.scl(speedHeap[0]);

		// Apply the parent's scale
		velocity.scl(scale);

		scene.addInstance(internalInstance);

		internalInstance.setTransformation(node.worldLocation, rotationHeap.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z,
				RenderMathUtils.randomInRange(0, (float) Math.PI * 2)), node.worldScale);
		internalInstance.setSequence(0);
		internalInstance.show();
	}

	@Override
	public void update(final float dt) {
		final MdxComplexInstance internalInstance = this.internalInstance;

		internalInstance.paused = false; /// Why is this here?

		this.health -= dt;

		if (this.health > 0) {
			final Vector3 velocity = this.velocity;

			velocity.z -= this.gravity * dt;

			tempVector[0] = velocity.x * dt;
			tempVector[1] = velocity.y * dt;
			tempVector[2] = velocity.z * dt;
			internalInstance.move(tempVector);
		}
	}
}
