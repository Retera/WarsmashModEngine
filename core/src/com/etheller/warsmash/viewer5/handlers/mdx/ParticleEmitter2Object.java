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
	public boolean head;
	public boolean tail;
	public float cellWidth;
	public float cellHeight;
	public float[][] colors;
	public float[] scaling;
	public float[][] intervals;
	public int blendSrc;
	public int blendDst;
	public int filterModeForSort;
	public int priorityPlane;

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

		this.head = headOrTail.isIncludesHead();
		this.tail = headOrTail.isIncludesTail();

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

		this.filterModeForSort = emitter.getFilterMode().ordinal() + 2;
		this.blendSrc = blendModes[0];
		this.blendDst = blendModes[1];

		this.priorityPlane = emitter.getPriorityPlane();
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
		return this.getScalarValue(out, AnimationMap.KP2V.getWar3id(), sequence, frame, counter, 1);
	}

	public int getVariation(final float[] out, final int sequence, final int frame, final int counter) {
		return this.getScalarValue(out, AnimationMap.KP2R.getWar3id(), sequence, frame, counter, this.variation);
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
