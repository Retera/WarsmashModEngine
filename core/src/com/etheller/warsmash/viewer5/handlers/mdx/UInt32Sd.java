package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.util.RenderMathUtils;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;

public class UInt32Sd extends Sd<long[]> {

	public UInt32Sd(final MdxModel model, final MdlxTimeline<long[]> timeline) {
		super(model, timeline, SdArrayDescriptor.LONG_ARRAY);
	}

	@Override
	protected long[] convertDefaultValue(final float[] defaultValue) {
		final long[] returnValue = new long[defaultValue.length];
		for (int i = 0; i < defaultValue.length; i++) {
			returnValue[i] = (long) defaultValue[i];
		}
		return returnValue;
	}

	@Override
	protected void copy(final long[] out, final long[] value) {
		out[0] = value[0];
	}

	@Override
	protected void interpolate(final long[] out, final long[][] values, final long[][] inTans, final long[][] outTans,
			final int start, final int end, final float t) {
		final long startValue = values[start][0];

		switch (this.interpolationType) {
		case 0:
			out[0] = startValue;
			break;
		case 1:
			out[0] = (long) RenderMathUtils.lerp(startValue, values[end][0], t);
			break;
		case 2:
			out[0] = (long) RenderMathUtils.hermite(startValue, (start < outTans.length) ? outTans[start][0] : 0,
					(start < inTans.length) ? inTans[end][0] : 0, values[end][0], t);
			break;
		case 3:
			out[0] = (long) RenderMathUtils.bezier(startValue, (start < outTans.length) ? outTans[start][0] : 0,
					(start < inTans.length) ? inTans[end][0] : 0, values[end][0], t);
			break;
		}

	}

}
