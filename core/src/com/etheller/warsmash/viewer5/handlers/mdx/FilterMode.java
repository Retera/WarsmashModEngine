package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.graphics.GL20;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter2;

public class FilterMode {
	private static final int[] ERROR_DEFAULT = new int[] { 0, 0 };
	private static final int[] MODULATE_2X = new int[] { GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR };
	private static final int[] MODULATE = new int[] { GL20.GL_ZERO, GL20.GL_SRC_COLOR };
	public static final int[] ADDITIVE_ALPHA = new int[] { GL20.GL_SRC_ALPHA, GL20.GL_ONE };
	private static final int[] BLEND = new int[] { GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA };

	public static int[] layerFilterMode(final MdlxLayer.FilterMode filterMode) {
		switch (filterMode) {
		case BLEND:
			return BLEND; // Blend
		case ADDITIVE:
			return ADDITIVE_ALPHA; // Additive
		case ADDALPHA:
			return ADDITIVE_ALPHA; // Add alpha
		case MODULATE:
			return MODULATE; // Modulate
		case MODULATE2X:
			return MODULATE_2X; // Modulate 2x
		default:
			return ERROR_DEFAULT;
		}
	}

	public static int[] emitterFilterMode(final MdlxParticleEmitter2.FilterMode filterMode) {
		switch (filterMode) {
		case BLEND:
			return BLEND; // Blend
		case ADDITIVE:
			return ADDITIVE_ALPHA; // Add alpha
		case MODULATE:
			return MODULATE; // Modulate
		case MODULATE2X:
			return MODULATE_2X; // Modulate 2x
		case ALPHAKEY:
			return BLEND; // Add alpha
		default:
			return ERROR_DEFAULT;
		}
	}
}
