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
	public final Vector3 scale = new Vector3(1, 1, 1);

	// WoW alpha (MDLX v1300) particle state; inert for v800 (Warcraft 3)
	// models.
	private boolean wowParticle = false;
	private float drag = 0;
	private final Vector3 windVelocity = new Vector3();
	private float windTime = 0;
	private final Vector3 twinkleBaseScale = new Vector3();
	private boolean twinkleActive = false;
	private int twinkleSeed = 0;

	private static final Quaternion rotationHeap = new Quaternion();
	private static final Quaternion rotationHeap2 = new Quaternion();
	private static final float[] widthHeap = new float[1];
	private static final float[] lengthHeap = new float[1];
	private static final float[] latitudeHeap = new float[1];
	private static final float[] variationHeap = new float[1];
	private static final float[] speedHeap = new float[1];
	private static final float[] gravityHeap = new float[1];
	private static final float[] longitudeHeap = new float[1];
	private static final float[] zSourceHeap = new float[1];
	private static final Vector3 directionHeap = new Vector3();

	public Particle2(final ParticleEmitter2 emitter) {
		super(emitter);
	}

	@Override
	protected void bind(final int flags) {
		final MdxComplexInstance instance = this.emitter.instance;
		final ParticleEmitter2Object emitterObject = this.emitter.emitterObject;

		emitterObject.getWidth(widthHeap, instance.sequence, instance.frame, instance.counter);
		emitterObject.getLength(lengthHeap, instance.sequence, instance.frame, instance.counter);
		emitterObject.getLatitude(latitudeHeap, instance.sequence, instance.frame, instance.counter);
		emitterObject.getVariation(variationHeap, instance.sequence, instance.frame, instance.counter);
		emitterObject.getSpeed(speedHeap, instance.sequence, instance.frame, instance.counter);
		emitterObject.getGravity(gravityHeap, instance.sequence, instance.frame, instance.counter);

		final MdxNode node = this.emitter.node;
		final Vector3 pivot = node.pivot;
		final Vector3 scale = node.worldScale;
		final float width = widthHeap[0] * 0.5f;
		final float length = lengthHeap[0] * 0.5f;
		final float variation = variationHeap[0];
		final float speed = speedHeap[0];
		final Vector3 location = this.location;
		final Vector3 velocity = this.velocity;

		this.health = emitterObject.lifeSpan;
		this.tail = flags;
		this.gravity = gravityHeap[0] * scale.z;
		this.wowParticle = emitterObject.wowParticleData;

		this.scale.set(scale);

		if (this.wowParticle) {
			// WoW alpha 0.5.3 (MDLX v1300) emission. Formulas follow the 0.5.3
			// client as reimplemented by barncastle/wow-mdx-viewer
			// (CPlaneParticleEmitter/CSphereParticleEmitter::CreateParticle)
			// and Deamon87/WebWowViewerCpp for the direct M2 descendants of
			// these fields; field docs at https://wowdev.wiki/MDX.
			emitterObject.getLongitude(longitudeHeap, instance.sequence, instance.frame, instance.counter);
			emitterObject.getZSource(zSourceHeap, instance.sequence, instance.frame, instance.counter);

			// v1300 latitude/longitude are stored in radians (v800 latitude is
			// stored in degrees).
			final float latitude = latitudeHeap[0];
			final float longitude = longitudeHeap[0];
			final float zSource = zSourceHeap[0];
			final float polar = RenderMathUtils.randomInRange(-latitude, latitude);
			final float azimuth = RenderMathUtils.randomInRange(-longitude, longitude);
			final Vector3 direction = directionHeap;

			if (emitterObject.emitterType == 2) {
				// Sphere emitter: spawn on a shell between the inner radius
				// (file "width" == lengthHeap here) and the outer radius (file
				// "length" == widthHeap here); the same direction is used for
				// the radial velocity. Note the polar angle is measured from
				// the XY plane for the spawn direction.
				final float innerRadius = lengthHeap[0];
				final float outerRadius = widthHeap[0];
				final float radius = RenderMathUtils.randomInRange(innerRadius, outerRadius);
				final float cosPolar = (float) Math.cos(polar);

				direction.set(cosPolar * (float) Math.cos(azimuth), cosPolar * (float) Math.sin(azimuth),
						(float) Math.sin(polar));
				location.x = pivot.x + (direction.x * radius);
				location.y = pivot.y + (direction.y * radius);
				location.z = pivot.z + (direction.z * radius);
			}
			else {
				// Plane (also Base, and the unimplemented Spline type):
				// rectangular spawn area, velocity in a polar/azimuth cone
				// around +Z.
				location.x = pivot.x + RenderMathUtils.randomInRange(-width, width);
				location.y = pivot.y + RenderMathUtils.randomInRange(-length, length);
				location.z = pivot.z;

				final float sinPolar = (float) Math.sin(polar);

				direction.set((float) Math.cos(azimuth) * sinPolar, (float) Math.sin(azimuth) * sinPolar,
						(float) Math.cos(polar));
			}

			if (zSource > 1e-6f) {
				// The initial velocity points from (0,0,zSource), in emitter
				// space, to the spawn position.
				direction.set(location.x - pivot.x, location.y - pivot.y, location.z - pivot.z - zSource);
				if (direction.len2() > 0) {
					direction.nor();
				}
			}

			// v1300 variation is a multiplier ("client adds 1.0 and multiplies
			// by random multiplier"), unlike v800 where it is an absolute
			// speed offset.
			final float wowSpeed = speed * (1 + RenderMathUtils.randomInRange(-variation, variation));

			velocity.set(direction).scl(wowSpeed);

			// Wind is an acceleration applied while the particle's remaining
			// life is below windTime; transform it like the velocity so that
			// it stays meaningful for rotated/scaled doodads.
			this.windTime = emitterObject.windActive ? emitterObject.windTime : 0;
			if (emitterObject.windActive) {
				this.windVelocity.set(emitterObject.windVector[0], emitterObject.windVector[1],
						emitterObject.windVector[2]);
			}
			else {
				this.windVelocity.set(0, 0, 0);
			}

			if (emitterObject.modelSpace == 0) {
				location.prj(node.worldMatrix);
				node.worldRotation.transform(velocity);
				velocity.scl(scale);
				if (emitterObject.windActive) {
					node.worldRotation.transform(this.windVelocity);
					this.windVelocity.scl(scale);
				}
			}

			this.drag = emitterObject.drag;
			this.twinkleActive = emitterObject.twinkleActive;
			if (this.twinkleActive) {
				this.twinkleBaseScale.set(this.scale);
				this.twinkleSeed = (int) (Math.random() * 0x7FFFFFFF);
			}
		}
		else {
			this.drag = 0;
			this.windTime = 0;
			this.twinkleActive = false;

			final float latitude = (float) Math.toRadians(latitudeHeap[0]);

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
			rotationHeap.mul(HALF_PI_Z);
			rotationHeap.mul(rotationHeap2.setFromAxisRad(0, 1, 0, RenderMathUtils.randomInRange(-latitude, latitude)));

			// If this is not a line emitter, emit in a sphere rather than a circle
			if (emitterObject.lineEmitter == 0) {
				rotationHeap.mul(rotationHeap2.setFromAxisRad(1, 0, 0, RenderMathUtils.randomInRange(-latitude, latitude)));
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
	}

	@Override
	public void update(final float dt) {
		this.health -= dt;

		if (this.health > 0) {
			if (this.wowParticle) {
				if (this.drag != 0) {
					// v = v * (1 - drag * dt), an exponential velocity decay
					// (M2: "Speed is multiplied by exp(-drag * t)").
					this.velocity.scl(Math.max(0, 1 - (this.drag * dt)));
				}
				if ((this.windTime > 0) && (this.health < this.windTime)) {
					this.velocity.x += this.windVelocity.x * dt;
					this.velocity.y += this.windVelocity.y * dt;
					this.velocity.z += this.windVelocity.z * dt;
				}
			}

			this.velocity.z -= this.gravity * dt;

			this.location.x += this.velocity.x * dt;
			this.location.y += this.velocity.y * dt;
			this.location.z += this.velocity.z * dt;

			if (this.twinkleActive) {
				updateTwinkle();
			}
		}
	}

	/**
	 * WoW alpha twinkle: re-roll a pseudo-random value at twinkleFPS Hz; the
	 * value scales the particle within [twinkleScaleMin, twinkleScaleMax] and
	 * hides it entirely when it exceeds the twinkleOnOff duty cycle (0.8 means
	 * visible ~80% of the time). Mirrors the M2 twinkle behavior
	 * (Deamon87/WebWowViewerCpp CalculateParticlePreRenderData), which uses one
	 * random table lookup per step for both the blink test and the scale.
	 */
	private void updateTwinkle() {
		final ParticleEmitter2Object emitterObject = this.emitter.emitterObject;
		final float age = emitterObject.lifeSpan - this.health;
		final int step = (int) (age * emitterObject.twinkleFPS) + this.twinkleSeed;
		final float random = hash01(step);

		if ((emitterObject.twinkleOnOff < 1) && (emitterObject.twinkleOnOff < random)) {
			// Blinked off for this twinkle step.
			this.scale.set(0, 0, 0);
		}
		else {
			final float scaleFactor = emitterObject.twinkleScaleMin
					+ ((emitterObject.twinkleScaleMax - emitterObject.twinkleScaleMin) * random);

			this.scale.set(this.twinkleBaseScale).scl(scaleFactor);
		}
	}

	/**
	 * Cheap integer scramble mapped to [0, 1); stands in for the client's
	 * 128-entry random table.
	 */
	private static float hash01(int x) {
		x = ((x >>> 16) ^ x) * 0x45d9f3b;
		x = ((x >>> 16) ^ x) * 0x45d9f3b;
		x = (x >>> 16) ^ x;
		return (x & 0xFFFF) / 65536f;
	}

}
