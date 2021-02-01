package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.Interpolator;
import com.etheller.warsmash.util.RenderMathUtils;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;

public class QuaternionSd extends Sd<float[]> {

	public QuaternionSd(final MdxModel model, final MdlxTimeline<float[]> timeline) {
		super(model, timeline, SdArrayDescriptor.FLOAT_ARRAY);
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
		Interpolator.interpolateQuaternion(out, values[start],
				(start < outTans.length) ? outTans[start] : RenderMathUtils.EMPTY_FLOAT_ARRAY,
				(start < inTans.length) ? inTans[end] : RenderMathUtils.EMPTY_FLOAT_ARRAY, values[end], t,
				this.interpolationType);
	}

}
