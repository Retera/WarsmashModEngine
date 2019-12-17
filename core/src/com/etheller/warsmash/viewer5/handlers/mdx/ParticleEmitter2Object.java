package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;

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
	public int priorityPlane;

	public ParticleEmitter2Object(final MdxModel model,
			final com.etheller.warsmash.parsers.mdlx.ParticleEmitter2 emitter, final int index) {
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

		if (this.replaceableId == 0) {
			this.internalTexture = model.getTextures().get(emitter.getTextureId());
		}
		else if ((replaceableId == 1) || (replaceableId == 2)) {
			this.teamColored = 1;
		}
		else {
			this.internalTexture = (Texture) model.viewer.load(
					"ReplaceableTextures\\" + ReplaceableIds.get(replaceableId) + ".blp", model.pathSolver,
					model.solverParams);
		}

		this.replaceableId = emitter.getReplaceableId();

		final long headOrTail = emitter.getHeadOrTail();

		this.head = ((headOrTail == 0) || (headOrTail == 2));
		this.tail = ((headOrTail == 1) || (headOrTail == 2));

		this.cellWidth = 1f / emitter.getColumns();
		this.cellHeight = 1f / emitter.getRows();
		this.colors = new float[3][0];

		final float[][] colors = emitter.getSegmentColors();
		final short[] alpha = emitter.getSegmentAlphas();

		for (int i = 0; i < 3; i++) {
			final float[] color = colors[i];

			this.colors[i] = new float[] { color[0], color[1], color[2], alpha[i] / 255f };
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

		this.blendSrc = blendModes[0];
		this.blendDst = blendModes[1];

		this.priorityPlane = emitter.getPriorityPlane();
	}

	public int getWidth(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2N.getWar3id(), instance, this.width);
	}

	public int getLength(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2W.getWar3id(), instance, this.length);
	}

	public int getSpeed(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2S.getWar3id(), instance, this.speed);
	}

	public int getLatitude(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2L.getWar3id(), instance, this.latitude);
	}

	public int getGravity(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2G.getWar3id(), instance, this.gravity);
	}

	public int getEmissionRate(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2E.getWar3id(), instance, this.emissionRate);
	}

	@Override
	public int getVisibility(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2V.getWar3id(), instance, 1);
	}

	public int getVariation(final float[] out, final MdxComplexInstance instance) {
		return this.getScalarValue(out, AnimationMap.KP2R.getWar3id(), instance, this.variation);
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
