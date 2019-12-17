package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.util.Interpolator;

public class VectorSd extends Sd<float[]> {

	public VectorSd(final MdxModel model, final Timeline<float[]> timeline) {
		super(model, timeline);
	}

	@Override
	protected float[] convertDefaultValue(final float[] defaultValue) {
		return defaultValue;
	}

	@Override
	protected void copy(final float[] out, final float[] value) {
		System.arraycopy(value, 0, out, 0, value.length);
	}

	@Override
	protected void interpolate(final float[] out, final float[][] values, final float[][] inTans,
			final float[][] outTans, final int start, final int end, final float t) {
		Interpolator.interpolateVector(out, values[start], outTans[start], inTans[end], values[end], t,
				this.interpolationType);
	}

}
