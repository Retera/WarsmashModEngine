package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public abstract class EventObjectEmitterObject extends GenericObject implements EmitterObject {
	private int geometryEmitterType = -1;
	private final String type;
	private final String id;
	private final long[] keyFrames;
	private long globalSequence;
	private final long[] defval = { 1 };
	private MdxModel internalModel;
	private Texture internalTexture;
	private float[][] colors;
	private float[] intervalTimes;
	private float scale;
	private int columns;
	private int rows;
	private float lifeSpan;
	private int blendSrc;
	private int blendDst;
	private float[][] intervals;
	private float distanceCutoff;
	private float maxDistance;
	private float minDistance;
	private float pitch;
	private float pitchVariance;
	private float volume;
//	private AudioBuffer[] decodedBuffers;
	/**
	 * If this is an SPL/UBR emitter object, ok will be set to true if the tables
	 * are loaded.
	 *
	 * This is because, like the other geometry emitters, it is fine to use them
	 * even if the textures don't load.
	 *
	 * The particles will simply be black.
	 */
	private final boolean ok = false;

	public EventObjectEmitterObject(final MdxModel model,
			final com.etheller.warsmash.parsers.mdlx.EventObject eventObject, final int index) {
		super(model, eventObject, index);

		final ModelViewer viewer = model.viewer;
		final String name = eventObject.getName();
		String type = name.substring(0, 3);
		final String id = name.substring(4);

		// Same thing
		if ("FPT".equals(type)) {
			type = "SPL";
		}

		if ("SPL".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_SPLAT;
		}
		else if ("UBR".equals(type)) {
			this.geometryEmitterType = GeometryEmitterFuncs.EMITTER_UBERSPLAT;
		}

		this.type = type;
		this.id = id;
		this.keyFrames = eventObject.getKeyFrames();
	}

}
