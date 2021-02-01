package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.Locale;

import com.etheller.warsmash.viewer5.handlers.EmitterObject;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter;

public class ParticleEmitterObject extends GenericObject implements EmitterObject {
	public MdxModel internalModel;
	public float speed;
	public float latitude;
	public float longitude;
	public float lifeSpan;
	public float gravity;
	public float emissionRate;

	/**
	 * No need to create instances of the internal model if it didn't load.
	 *
	 * Such instances won't actually render, and who knows if the model will ever
	 * load?
	 */
	public boolean ok = false;

	public ParticleEmitterObject(final MdxModel model, final MdlxParticleEmitter emitter, final int index) {
		super(model, emitter, index);

		this.internalModel = (MdxModel) model.viewer.load(
				emitter.getPath().replace("\\", "/").toLowerCase(Locale.US).replace(".mdl", ".mdx"), model.pathSolver,
				model.solverParams);
		this.speed = emitter.getSpeed();
		this.latitude = emitter.getLatitude();
		this.longitude = emitter.getLongitude();
		this.lifeSpan = emitter.getLifeSpan();
		this.gravity = emitter.getGravity();
		this.emissionRate = emitter.getEmissionRate();

		// Activate emitters based on this emitter object only when and if the internal
		// model loads successfully.
		// TODO async removed here
		this.ok = this.internalModel.ok;
	}

	public int getSpeed(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPES.getWar3id(), sequence, frame, counter, this.speed);
	}

	public int getLatitude(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPLT.getWar3id(), sequence, frame, counter, this.latitude);
	}

	public int getLongitude(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPLN.getWar3id(), sequence, frame, counter, this.longitude);
	}

	public int getLifeSpan(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPEL.getWar3id(), sequence, frame, counter, this.lifeSpan);
	}

	public int getGravity(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPEG.getWar3id(), sequence, frame, counter, this.gravity);
	}

	public int getEmissionRate(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPEE.getWar3id(), sequence, frame, counter, this.emissionRate);
	}

	@Override
	public int getVisibility(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPEV.getWar3id(), sequence, frame, counter, 1);
	}

	@Override
	public int getGeometryEmitterType() {
		throw new UnsupportedOperationException("ghostwolf doesnt have this in the JS");
	}

	@Override
	public boolean ok() {
		return this.ok;
	}
}
