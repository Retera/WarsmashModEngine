package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.parsers.mdlx.timeline.FloatArrayTimeline;
import com.etheller.warsmash.parsers.mdlx.timeline.FloatTimeline;
import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.parsers.mdlx.timeline.UInt32Timeline;
import com.etheller.warsmash.util.War3ID;

public class AnimatedObject {
	public MdxModel model;
	public Map<War3ID, Sd<?>> timelines;

	public AnimatedObject(final MdxModel model, final com.etheller.warsmash.parsers.mdlx.AnimatedObject object) {
		this.model = model;
		this.timelines = new HashMap<>();

		for (final Timeline<?> timeline : object.getTimelines()) {
			this.timelines.put(timeline.getName(), createTypedSd(model, timeline));
		}
	}

	public int getScalarValue(final float[] out, final War3ID name, final MdxComplexInstance instance,
			final float defaultValue) {
		final Sd<?> animation = this.timelines.get(name);

		if (animation instanceof ScalarSd) {
			return ((ScalarSd) animation).getValue(out, instance);
		}

		out[0] = defaultValue;

		return -1;
	}

	public int getScalarValue(final long[] out, final War3ID name, final MdxComplexInstance instance,
			final long defaultValue) {
		final Sd<?> animation = this.timelines.get(name);

		if (animation instanceof UInt32Sd) {
			return ((UInt32Sd) animation).getValue(out, instance);
		}

		out[0] = defaultValue;

		return -1;
	}

	public int getVectorValue(final float[] out, final War3ID name, final MdxComplexInstance instance,
			final float[] defaultValue) {
		final Sd<?> animation = this.timelines.get(name);

		if (animation instanceof VectorSd) {
			return ((VectorSd) animation).getValue(out, instance);
		}

		System.arraycopy(defaultValue, 0, out, 0, 3);

		return -1;
	}

	public int getQuadValue(final float[] out, final War3ID name, final MdxComplexInstance instance,
			final float[] defaultValue) {
		final Sd<?> animation = this.timelines.get(name);

		if (animation instanceof QuaternionSd) {
			return ((QuaternionSd) animation).getValue(out, instance);
		}

		System.arraycopy(defaultValue, 0, out, 0, 4);

		return -1;
	}

	public boolean isVariant(final War3ID name, final int sequence) {
		final Sd<?> timeline = this.timelines.get(name);

		if (timeline != null) {
			return timeline.isVariant(sequence);
		}

		return false;
	}

	private Sd<?> createTypedSd(final MdxModel model, final Timeline<?> timeline) {
		if (timeline instanceof UInt32Timeline) {
			return new UInt32Sd(model, (UInt32Timeline) timeline);
		}
		else if (timeline instanceof FloatTimeline) {
			return new ScalarSd(model, (FloatTimeline) timeline);
		}
		else if (timeline instanceof FloatArrayTimeline) {
			final FloatArrayTimeline faTimeline = (FloatArrayTimeline) timeline;
			final int arraySize = faTimeline.getArraySize();
			if (arraySize == 3) {
				return new VectorSd(model, faTimeline);
			}
			else if (arraySize == 4) {
				return new QuaternionSd(model, faTimeline);
			}
			else {
				throw new IllegalStateException("Unsupported arraySize = " + arraySize);
			}
		}
		throw new IllegalStateException("Unsupported timeline type " + timeline.getClass());
	}
}
