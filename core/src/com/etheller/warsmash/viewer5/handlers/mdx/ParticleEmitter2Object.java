package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter2;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter2.HeadOrTail;

public class ParticleEmitter2Object extends GenericObject implements EmitterObject {
	public float width;
	public float length;
	public float speed;
	public float latitude;
	public float gravity;
	public float emissionRate;
	public long squirt;
	public float lifeSpan;
	public float variation;
	public float tailLength;
	public float timeMiddle;
	public long columns;
	public long rows;
	public int teamColored = 0;
	public Texture internalTexture;
	public long replaceableId;
	public HeadOrTail headOrTail;
	public float cellWidth;
	public float cellHeight;
	public float[][] colors;
	public float[] scaling;
	public float[][] intervals;
	public int blendSrc;
	public int blendDst;
	public int filterModeForShader;
	public int filterModeForSort;
	public int priorityPlane;

	// MDLX v1300 (WoW alpha) extra emitter data. All of these default to
	// neutral values for v800 (Warcraft 3) models, whose behavior must remain
	// unchanged; "wowParticleData" is the master guard flag.
	public boolean wowParticleData = false;
	/** v1300 emitter type: 1 = Plane, 2 = Sphere, 3 = Spline. */
	public long emitterType = 1;
	/** Azimuth emission range in radians (v1300 latitude is also radians). */
	public float longitude;
	/**
	 * When nonzero, particle velocity points from (0,0,zSource) to the spawn
	 * position instead of using the latitude/longitude cone.
	 */
	public float zSource;
	/** Velocity decay coefficient per second. */
	public float drag;
	/** World-space wind acceleration applied to particles. */
	public final float[] windVector = new float[3];
	/** Duration in seconds during which the wind is applied. */
	public float windTime;
	/**
	 * Parsed-but-not-rendered v1300 fields (no confirmed-safe use in this
	 * renderer yet): twinkle scaling, per-particle tumble/spin rotation
	 * (needs shader support), emitter-motion follow scaling, initial velocity
	 * inheritance scale, and spline emission points.
	 */
	public float twinkleFPS;
	public float twinkleOnOff;
	public float twinkleScaleMin;
	public float twinkleScaleMax;
	public float ivelScale;
	public float tumbleXMin, tumbleXMax, tumbleYMin, tumbleYMax, tumbleZMin, tumbleZMax;
	public float spin;
	public float followSpeed1, followScale1, followSpeed2, followScale2;
	public float[] splineData;
	/** True when the twinkle values would have a visible effect. */
	public boolean twinkleActive = false;
	/** True when the wind vector would have a visible effect. */
	public boolean windActive = false;

	public ParticleEmitter2Object(final MdxModel model, final MdlxParticleEmitter2 emitter, final int index) {
		super(model, emitter, index);
		this.width = emitter.getWidth();
		this.length = emitter.getLength();
		this.speed = emitter.getSpeed();
		this.latitude = emitter.getLatitude();
		this.gravity = emitter.getGravity();
		this.emissionRate = emitter.getEmissionRate();
		this.squirt = emitter.getSquirt();
		this.lifeSpan = emitter.getLifeSpan();
		this.variation = emitter.getVariation();
		this.tailLength = emitter.getTailLength();
		this.timeMiddle = emitter.getTimeMiddle();

		final long replaceableId = emitter.getReplaceableId();

		this.columns = emitter.getColumns();
		this.rows = emitter.getRows();

		if (replaceableId == 0) {
			this.internalTexture = model.getTextures().get(emitter.getTextureId());
		}
		else if ((replaceableId == 1) || (replaceableId == 2)) {
			this.teamColored = 1;
		}
		else {
			this.internalTexture = (Texture) model.viewer.load(
					"ReplaceableTextures\\" + ReplaceableIds.getPathString(replaceableId) + ".blp", model.pathSolver,
					model.solverParams);
		}

		this.replaceableId = emitter.getReplaceableId();

		final HeadOrTail headOrTail = emitter.getHeadOrTail();

		this.headOrTail = headOrTail;

		this.cellWidth = 1f / emitter.getColumns();
		this.cellHeight = 1f / emitter.getRows();
		this.colors = new float[3][0];

		final float[][] colors = emitter.getSegmentColors();
		final short[] alpha = emitter.getSegmentAlphas();

		for (int i = 0; i < 3; i++) {
			final float[] color = colors[i];

			this.colors[i] = new float[] { color[0], color[1], color[2],
					(alpha[i] / 255f) * WarsmashConstants.MODEL_DETAIL_PARTICLE_FACTOR_INVERSE };
		}

		this.scaling = emitter.getSegmentScaling();

		final long[][] headIntervals = emitter.getHeadIntervals();
		final long[][] tailIntervals = emitter.getTailIntervals();

		// Change to Float32Array instead of Uint32Array to be able to pass the
		// intervals directly using uniform3fv().
		this.intervals = new float[][] { { headIntervals[0][0], headIntervals[0][1], headIntervals[0][2] },
				{ headIntervals[1][0], headIntervals[1][1], headIntervals[1][2] },
				{ tailIntervals[0][0], tailIntervals[0][1], tailIntervals[0][2] },
				{ tailIntervals[1][0], tailIntervals[1][1], tailIntervals[1][2] }, };

		final int[] blendModes = FilterMode.emitterFilterMode(emitter.getFilterMode());

		this.filterModeForShader = emitter.getFilterMode().ordinal();
		this.filterModeForSort = emitter.getFilterMode().ordinal() + 2;
		this.blendSrc = blendModes[0];
		this.blendDst = blendModes[1];

		this.priorityPlane = emitter.getPriorityPlane();

		if (model.wow) {
			// WoW alpha 0.5.3 (MDLX version 1300) particle emitter data.
			this.wowParticleData = true;
			this.emitterType = emitter.emitterType;
			this.longitude = filteredWowValue(emitter.longitude);
			this.zSource = filteredWowValue(emitter.zSource);
			this.drag = filteredWowValue(emitter.drag);
			this.windVector[0] = filteredWowValue(emitter.windVector[0]);
			this.windVector[1] = filteredWowValue(emitter.windVector[1]);
			this.windVector[2] = filteredWowValue(emitter.windVector[2]);
			this.windTime = filteredWowValue(emitter.windTime);
			this.twinkleFPS = filteredWowValue(emitter.twinkleFPS);
			this.twinkleOnOff = filteredWowValue(emitter.twinkleOnOff);
			this.twinkleScaleMin = filteredWowValue(emitter.twinkleScaleMin);
			this.twinkleScaleMax = filteredWowValue(emitter.twinkleScaleMax);
			this.ivelScale = filteredWowValue(emitter.ivelScale);
			this.tumbleXMin = filteredWowValue(emitter.tumblexMin);
			this.tumbleXMax = filteredWowValue(emitter.tumblexMax);
			this.tumbleYMin = filteredWowValue(emitter.tumbleyMin);
			this.tumbleYMax = filteredWowValue(emitter.tumbleyMax);
			this.tumbleZMin = filteredWowValue(emitter.tumblezMin);
			this.tumbleZMax = filteredWowValue(emitter.tumblezMax);
			this.spin = filteredWowValue(emitter.spin);
			this.followSpeed1 = filteredWowValue(emitter.followSpeed1);
			this.followScale1 = filteredWowValue(emitter.followScale1);
			this.followSpeed2 = filteredWowValue(emitter.followSpeed2);
			this.followScale2 = filteredWowValue(emitter.followScale2);
			this.splineData = emitter.splineData;

			// Twinkle re-rolls a random value at twinkleFPS Hz; the value both
			// scales the particle within [twinkleScaleMin, twinkleScaleMax] and
			// blinks it off when it exceeds the twinkleOnOff duty cycle
			// (lineage: M2 twinkleSpeed/twinklePercent/twinkleScale, see
			// https://wowdev.wiki/M2#Particle_emitters). Inert unless enabled
			// (twinkleOnOff > 0) and either the scale range is nonempty or the
			// duty cycle is below 1.
			this.twinkleActive = (this.twinkleOnOff > 0)
					&& ((Math.abs(this.twinkleScaleMax - this.twinkleScaleMin) > 1e-6f) || (this.twinkleOnOff < 1));
			this.windActive = (this.windTime > 0) && ((this.windVector[0] != 0) || (this.windVector[1] != 0)
					|| (this.windVector[2] != 0));
		}
	}

	/**
	 * A few of the alpha assets contain uninitialized garbage in rarely-used
	 * emitter fields (for example NaN twinkle values in
	 * Spells\SavageBlow_Impact_Chest.mdx); treat anything non-finite as zero.
	 */
	private static float filteredWowValue(final float value) {
		return Float.isFinite(value) ? value : 0;
	}

	public int getWidth(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2N.getWar3id(), sequence, frame, counter, this.length);
	}

	public int getLength(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2W.getWar3id(), sequence, frame, counter, this.width);
	}

	public int getSpeed(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2S.getWar3id(), sequence, frame, counter, this.speed);
	}

	public int getLatitude(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2L.getWar3id(), sequence, frame, counter, this.latitude);
	}

	public int getGravity(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2G.getWar3id(), sequence, frame, counter, this.gravity);
	}

	public int getEmissionRate(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2E.getWar3id(), sequence, frame, counter, this.emissionRate);
	}

	@Override
	public int getVisibility(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KVIS.getWar3id(), sequence, frame, counter, 1);
	}

	public int getVariation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2R.getWar3id(), sequence, frame, counter, this.variation);
	}

	/** v1300 (WoW alpha) only: azimuth emission range, animated with KPLN. */
	public int getLongitude(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KPLN.getWar3id(), sequence, frame, counter, this.longitude);
	}

	/** v1300 (WoW alpha) only: z source, animated with KP2Z. */
	public int getZSource(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2Z.getWar3id(), sequence, frame, counter, this.zSource);
	}

	@Override
	public boolean ok() {
		return true;
	}

	@Override
	public int getGeometryEmitterType() {
		return GeometryEmitterFuncs.EMITTER_PARTICLE2;
	}

}
