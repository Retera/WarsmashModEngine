package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.MdlxAnimatedObject;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatArrayTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxUInt32Timeline;

public class AnimatedObject {
	public MdxModel model;
	public Map<War3ID, Sd<?>> timelines;
	public Map<String, byte[]> variants;

	public AnimatedObject(final MdxModel model, final MdlxAnimatedObject object) {
		this.model = model;
		this.timelines = new HashMap<>();
		this.variants = new HashMap<>();

		for (final MdlxTimeline<?> timeline : object.getTimelines()) {
			this.timelines.put(timeline.getName(), createTypedSd(model, timeline));
		}
	}

	public int getScalarValue(final float[] out, final War3ID name, final int sequence, final int frame,
			final int counter, final float defaultValue) {
		if (sequence != -1) {
			final Sd<?> animation = this.timelines.get(name);

			if (animation instanceof ScalarSd) {
				return ((ScalarSd) animation).getValue(out, sequence, frame, counter);
			}
		}

		out[0] = defaultValue;

		return -1;
	}

	public int getScalarValue(final long[] out, final War3ID name, final int sequence, final int frame,
			final int counter, final long defaultValue) {
		if (sequence != -1) {
			final Sd<?> animation = this.timelines.get(name);

			if (animation instanceof UInt32Sd) {
				return ((UInt32Sd) animation).getValue(out, sequence, frame, counter);
			}
		}

		out[0] = defaultValue;

		return -1;
	}

	public int getVectorValue(final float[] out, final War3ID name, final int sequence, final int frame,
			final int counter, final float[] defaultValue) {
		if (sequence != -1) {
			final Sd<?> animation = this.timelines.get(name);

			if (animation instanceof VectorSd) {
				return ((VectorSd) animation).getValue(out, sequence, frame, counter);
			}
		}

		System.arraycopy(defaultValue, 0, out, 0, 3);

		return -1;
	}

	public int getQuatValue(final float[] out, final War3ID name, final int sequence, final int frame,
			final int counter, final float[] defaultValue) {
		if (sequence != -1) {
			final Sd<?> animation = this.timelines.get(name);

			if (animation instanceof QuaternionSd) {
				return ((QuaternionSd) animation).getValue(out, sequence, frame, counter);
			}
		}

		System.arraycopy(defaultValue, 0, out, 0, 4);

		return -1;
	}

	public void addVariants(final War3ID name, final String variantName) {
		final Sd<?> timeline = this.timelines.get(name);
		final int sequences = this.model.getSequences().size();
		final byte[] variants = new byte[sequences];

		if (timeline != null) {
			for (int i = 0; i < sequences; i++) {
				if (timeline.isVariant(i)) {
					variants[i] = 1;
				}
			}
		}

		this.variants.put(variantName, variants);
	}

	public void addVariantIntersection(final String[] names, final String variantName) {
		final int sequences = this.model.getSequences().size();
		final byte[] variants = new byte[sequences];

		for (int i = 0; i < sequences; i++) {
			for (final String name : names) {
				final byte[] variantsAtName = this.variants.get(name);
				if ((variantsAtName != null) && (variantsAtName[i] != 0)) {
					variants[i] = 1;
				}
			}
		}

		this.variants.put(variantName, variants);
	}

	public boolean isVariant(final War3ID name, final int sequence) {
		final Sd<?> timeline = this.timelines.get(name);

		if (timeline != null) {
			return timeline.isVariant(sequence);
		}

		return false;
	}

	private Sd<?> createTypedSd(final MdxModel model, final MdlxTimeline<?> timeline) {
		if (timeline instanceof MdlxUInt32Timeline) {
			return new UInt32Sd(model, (MdlxUInt32Timeline) timeline);
		}
		else if (timeline instanceof MdlxFloatTimeline) {
			return new ScalarSd(model, (MdlxFloatTimeline) timeline);
		}
		else if (timeline instanceof MdlxFloatArrayTimeline) {
			final MdlxFloatArrayTimeline faTimeline = (MdlxFloatArrayTimeline) timeline;
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
