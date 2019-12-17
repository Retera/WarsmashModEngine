package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.EmittedObject;

public class Particle2 extends EmittedObject<MdxComplexInstance, ParticleEmitter2> {
	private static final Quaternion HALF_PI_Z = new Quaternion().setFromAxisRad(0, 0, 1, (float) (Math.PI / 2));
	public int tail = 0;
	private float gravity = 0;
	public final Vector3 location = new Vector3();
	public final Vector3 velocity = new Vector3();
	public final Vector3 scale = new Vector3();
	private final ParticleEmitter2 emitter2;

	private static final Quaternion rotationHeap = new Quaternion();
	private static final Quaternion rotationHeap2 = new Quaternion();
	private static final float[] widthHeap = new float[1];
	private static final float[] lengthHeap = new float[1];
	private static final float[] latitudeHeap = new float[1];
	private static final float[] variationHeap = new float[1];
	private static final float[] speedHeap = new float[1];
	private static final float[] gravityHeap = new float[1];

	public Particle2(final ParticleEmitter2 emitter) {
		this.emitter2 = emitter;
	}

	@Override
	protected void bind(final int flags) {
		final MdxComplexInstance instance = this.emitter.instance;
		final ParticleEmitter2Object emitterObject = this.emitter.emitterObject;

		emitterObject.getWidth(widthHeap, instance);
		emitterObject.getLength(lengthHeap, instance);
		emitterObject.getLatitude(latitudeHeap, instance);
		emitterObject.getVariation(variationHeap, instance);
		emitterObject.getSpeed(speedHeap, instance);
		emitterObject.getGravity(gravityHeap, instance);

		final MdxNode node = this.emitter.node;
		final Vector3 pivot = node.pivot;
		final Vector3 scale = node.worldScale;
		final float width = widthHeap[0] * 0.5f;
		final float length = lengthHeap[0] * 0.5f;
		final float latitude = (float) Math.toRadians(latitudeHeap[0]);
		final float variation = variationHeap[0];
		final float speed = speedHeap[0];
		final Vector3 location = this.location;
		final Vector3 velocity = this.velocity;

		this.health = emitterObject.lifeSpan;
		this.tail = flags;
		this.gravity = gravityHeap[0] * scale.z;

		this.scale.set(scale);

		// Local location
		location.x = pivot.x + RenderMathUtils.randomInRange(-width, width);
		location.y = pivot.y + RenderMathUtils.randomInRange(-length, length);
		location.z = pivot.z;

		// World location
		if (emitterObject.modelSpace == 0) {
			location.prj(node.worldMatrix);
		}

		// Local rotation
		rotationHeap.idt();
		rotationHeap.mulLeft(HALF_PI_Z);
		rotationHeap.mulLeft(rotationHeap2.setFromAxisRad(0, 1, 0, RenderMathUtils.randomInRange(-latitude, latitude)));

		// If this is not a line emitter, emit in a sphere rather than a circle
		if (emitterObject.lineEmitter == 0) {
			rotationHeap
					.mulLeft(rotationHeap2.setFromAxisRad(1, 0, 0, RenderMathUtils.randomInRange(-latitude, latitude)));
		}

		// World rotation
		if (emitterObject.modelSpace == 0) {
			rotationHeap.mulLeft(node.worldRotation);
		}

		// Apply the rotation
		velocity.set(RenderMathUtils.VEC3_UNIT_Z);
		rotationHeap.transform(velocity);

		// Apply speed
		velocity.scl(speed + RenderMathUtils.randomInRange(-variation, variation));

		// Apply the parent's scale
		if (emitterObject.modelSpace == 0) {
			velocity.scl(scale);
		}
	}

	@Override
	public void update(final float dt) {
		this.health -= dt;

		if (this.health > 0) {
			this.velocity.z -= this.gravity * dt;

			this.location.x += this.velocity.x * dt;
			this.location.y += this.velocity.y * dt;
			this.location.z += this.velocity.z * dt;
		}
	}

}
