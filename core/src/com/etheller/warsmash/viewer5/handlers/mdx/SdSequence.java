package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.Arrays;

import com.etheller.warsmash.util.ParseUtils;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;

public final class SdSequence<TYPE> {
	private static boolean INJECT_FRAMES_GHOSTWOLF_STYLE = false;

	private final Sd<TYPE> sd;
	public final long start; // UInt32
	public final long end; // UInt32
	public long[] frames;
	public TYPE[] values;
	public TYPE[] inTans;
	public TYPE[] outTans;
	public boolean constant;

	public SdSequence(final Sd<TYPE> sd, final long start, final long end, final MdlxTimeline<TYPE> timeline,
			final boolean isGlobalSequence, final SdArrayDescriptor<TYPE> arrayDescriptor) {
		this.sd = sd;
		this.start = start;
		this.end = end;
		final ArrayList<Long> framesBuilder = new ArrayList<>();
		final ArrayList<TYPE> valuesBuilder = new ArrayList<>();
		final ArrayList<TYPE> inTansBuilder = new ArrayList<>();
		final ArrayList<TYPE> outTansBuilder = new ArrayList<>();
		this.constant = false;

		final int interpolationType = sd.interpolationType;
		final long[] frames = timeline.getFrames();
		final TYPE[] values = getValues(timeline);
		final TYPE[] inTans = getInTans(timeline);
		final TYPE[] outTans = getOutTans(timeline);
		final TYPE defval = sd.defval;

		// When using a global sequence, where the first key is outside of the
		// sequence's length, it becomes its constant value.
		// When having one key in the sequence's range, and one key outside of
		// it, results seem to be non-deterministic.
		// Sometimes the second key is used too, sometimes not.
		// It also differs depending where the model is viewed - the WE
		// previewer, the WE itself, or the game.
		// All three show different results, none of them make sense.
		// Therefore, only handle the case where the first key is outside.
		// This fixes problems spread over many models, e.g. HeroMountainKing
		// (compare in WE and in Magos).
		if (isGlobalSequence && (frames.length > 0) && (frames[0] > end)) {
			if (start == end) {
				framesBuilder.add(start);
			}
			else {
				framesBuilder.add(frames[0]);
			}
			valuesBuilder.add(values[0]);
		}

		// Go over the keyframes, and add all of the ones that are in this
		// sequence (start <= frame <= end).
		for (int i = 0, l = frames.length; i < l; i++) {
			final long frame = frames[i];

			if ((frame >= start) && (frame <= end)) {
				framesBuilder.add(frame);
				valuesBuilder.add(values[i]);

				if (interpolationType > 1) {
					inTansBuilder.add(inTans[i]);
					outTansBuilder.add(outTans[i]);
				}
			}
		}

		final int keyframeCount = framesBuilder.size();

		if (keyframeCount == 0) {
			// if there are no keys, use the default value directly.
			this.constant = true;
			framesBuilder.add(start);
			valuesBuilder.add(defval);
		}
		else if (keyframeCount == 1) {
			// If there's only one key, use it directly
			this.constant = true;
		}
		else {
			final TYPE firstValue = valuesBuilder.get(0);

			// If all of the values in this sequence are the same, might as well
			// make it constant.
			boolean allFramesMatch = true;
			for (final TYPE value : valuesBuilder) {
				if (!equals(firstValue, value)) {
					allFramesMatch = false;
				}
			}
			this.constant = allFramesMatch;

			if (!this.constant && INJECT_FRAMES_GHOSTWOLF_STYLE) {
				// If there is no opening keyframe for this sequence, inject one
				// with the default value.
				final boolean hasStart = framesBuilder.get(0) == start;
				if (!hasStart) {
					framesBuilder.add(start);
					valuesBuilder.add(defval);

					if (interpolationType > 1) {
						inTansBuilder.add(defval);
						outTansBuilder.add(defval);
					}
				}

				// If there is no closing keyframe for this sequence, inject one
				// with the default value.
				if (framesBuilder.get(framesBuilder.size() - 1) != end) {
					framesBuilder.add(end);
					final int sourceIndex = hasStart ? 0 : (valuesBuilder.size() - 1);
					valuesBuilder.add(valuesBuilder.get(sourceIndex));

					if (interpolationType > 1) {
						inTansBuilder.add(inTansBuilder.get(sourceIndex));
						outTansBuilder.add(outTansBuilder.get(sourceIndex));
					}
				}
			}
		}
		this.frames = new long[framesBuilder.size()];
		for (int i = 0; i < framesBuilder.size(); i++) {
			this.frames[i] = framesBuilder.get(i);
		}
		this.values = valuesBuilder.toArray(arrayDescriptor.create(valuesBuilder.size()));
		this.inTans = inTansBuilder.toArray(arrayDescriptor.create(inTansBuilder.size()));
		this.outTans = outTansBuilder.toArray(arrayDescriptor.create(outTansBuilder.size()));
	}

	private TYPE[] getValues(final MdlxTimeline<TYPE> timeline) {
		final TYPE[] values = timeline.getValues();
		return fixTimelineArray(timeline, values);
	}

	private TYPE[] getOutTans(final MdlxTimeline<TYPE> timeline) {
		final TYPE[] outTans = timeline.getOutTans();
		return fixTimelineArray(timeline, outTans);
	}

	private TYPE[] getInTans(final MdlxTimeline<TYPE> timeline) {
		final TYPE[] inTans = timeline.getInTans();
		return fixTimelineArray(timeline, inTans);
	}

	private TYPE[] fixTimelineArray(final MdlxTimeline<TYPE> timeline, final TYPE[] values) {
		if (values == null) {
			return null;
		}
		if (timeline.getName().equals(AnimationMap.KLAC.getWar3id())
				|| timeline.getName().equals(AnimationMap.KLBC.getWar3id())) {
			final float[][] flippedColorData = new float[values.length][3];
			for (int i = 0; i < values.length; i++) {
				flippedColorData[i] = ParseUtils.newFlippedRGB((float[]) values[i]);
			}
			return (TYPE[]) flippedColorData;
		}
		return values;
	}

	public int getValue(final TYPE out, final long frame) {
		final int length = this.frames.length;

		if (this.constant || (frame < this.start)) {
			this.sd.copy(out, this.values[0]);

			return -1;
		}
		else {
			int startFrameIndex = -1;
			int endFrameIndex = -1;
			final int lengthLessOne = length - 1;
			if ((frame < this.frames[0]) || (frame >= this.frames[lengthLessOne])) {
				startFrameIndex = lengthLessOne;
				endFrameIndex = 0;
			}
			else {
				for (int i = 1; i < length; i++) {
					if (this.frames[i] > frame) {
						startFrameIndex = i - 1;
						endFrameIndex = i;
						break;
					}
				}
			}
			long startFrame = this.frames[startFrameIndex];
			final long endFrame = this.frames[endFrameIndex];
			long timeBetweenFrames = endFrame - startFrame;
			if (timeBetweenFrames < 0) {
				timeBetweenFrames += (this.end - this.start);
				if (frame < startFrame) {
					startFrame = endFrame;
				}
			}
			final float t = ((timeBetweenFrames) == 0 ? 0 : ((frame - startFrame) / (float) (timeBetweenFrames)));
			this.sd.interpolate(out, this.values, this.inTans, this.outTans, startFrameIndex, endFrameIndex, t);
			return startFrameIndex;
		}
	}

	protected final boolean equals(final TYPE a, final TYPE b) {
		if ((a instanceof Float) && (b instanceof Float)) {
			return a.equals(b);
		}
		else if ((a instanceof Long) && (b instanceof Long)) {
			return a.equals(b);
		}
		else if ((a instanceof float[]) && (b instanceof float[])) {
			return Arrays.equals(((float[]) a), (float[]) b);
		}
		else if ((a instanceof long[]) && (b instanceof long[])) {
			return Arrays.equals(((long[]) a), (long[]) b);
		}
		return false;
	}
}
