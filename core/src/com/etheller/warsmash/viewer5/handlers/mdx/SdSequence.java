package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.mdlx.timeline.KeyFrame;
import com.etheller.warsmash.util.RenderMathUtils;

public abstract class SdSequence<TYPE> {

	private final Sd<TYPE> sd;
	private final long start; // UInt32
	private final long end; // UInt32
	private final List<KeyFrame> keyframes;
	private boolean constant;

	public SdSequence(final Sd<TYPE> sd, final long start, final long end, final List<KeyFrame> keyframes,
			final boolean isGlobalSequence) {
		final TYPE defval = convertDefaultValue(sd.defval);

		this.sd = sd;
		this.start = start;
		this.end = end;
		this.keyframes = new ArrayList<>();

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
		if (isGlobalSequence && keyframes.size() > 0 && keyframes.get(0).getTime() > end) {
			this.keyframes.add(keyframes.get(0));
		}

		// Go over the keyframes, and add all of the ones that are in this
		// sequence (start <= frame <= end).
		for (int i = 0, l = keyframes.size(); i < l; i++) {
			final KeyFrame keyFrame = keyframes.get(i);
			final long frame = keyFrame.getTime();

			if (frame >= start && frame <= end) {
				this.keyframes.add(keyFrame);
			}
		}

		final int keyframeCount = this.keyframes.size();

		if (keyframeCount == 0) {
			// if there are no keys, use the default value directly.
			this.constant = true;
			this.keyframes.add(createKeyFrame(start, defval));
		}
		else if (keyframeCount == 1) {
			// If there's only one key, use it directly
			this.constant = true;
		}
		else {
			final KeyFrame firstFrame = this.keyframes.get(0);

			// If all of the values in this sequence are the same, might as well
			// make it constant.
			boolean allFramesMatch = true;
			for (final KeyFrame frame : this.keyframes) {
				if (!frame.matchingValue(firstFrame)) {
					allFramesMatch = false;
				}
			}
			this.constant = allFramesMatch;

			if (!this.constant) {
				// If there is no opening keyframe for this sequence, inject one
				// with the default value.
				if (this.keyframes.get(0).getTime() != start) {
					this.keyframes.add(0, createKeyFrame(start, defval));
				}

				// If there is no closing keyframe for this sequence, inject one
				// with the default value.
				if (this.keyframes.get(this.keyframes.size() - 1).getTime() != end) {
					this.keyframes.add(this.keyframes.get(0).clone(end));
				}
			}
		}
	}

	public int getValue(final TYPE out, final long frame) {
		final int index = this.getKeyframe(frame);
		final int size = keyframes.size();

		if (index == -1) {
			set(out, keyframes.get(0));

			return 0;
		}
		else if (index == size) {
			set(out, keyframes.get(size - 1));

			return size - 1;
		}
		else {
			final KeyFrame start = keyframes.get(index - 1);
			final KeyFrame end = keyframes.get(index);
			final float t = RenderMathUtils.clamp((frame - start.getTime()) / (end.getTime() - start.getTime()), 0, 1);

			interpolate(out, start, end, t);

			return index;
		}
	}

	public int getKeyframe(final long frame) {
		if (this.constant) {
			return -1;
		}
		else {
			final int l = keyframes.size();

			if (frame < this.start) {
				return -1;
			}
			else if (frame >= this.end) {
				return 1;
			}
			else {
				for (int i = 1; i < l; i++) {
					final KeyFrame keyframe = keyframes.get(i);

					if (keyframe.getTime() > frame) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	protected abstract void set(TYPE out, KeyFrame frameForValue);

	protected abstract TYPE convertDefaultValue(float[] defaultValue);

	protected abstract KeyFrame createKeyFrame(long time, TYPE value);

	protected abstract void interpolate(TYPE out, KeyFrame a, KeyFrame b, float t);
}
