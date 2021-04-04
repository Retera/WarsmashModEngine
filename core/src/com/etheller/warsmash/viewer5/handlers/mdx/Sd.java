package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;

public abstract class Sd<TYPE> {
	public MdxModel model;
	public int interpolationType;
	public War3ID name;
	public TYPE defval;
	public SdSequence<TYPE> globalSequence;
	public List<SdSequence<TYPE>> sequences;

	public static Map<War3ID, Integer> forcedInterpMap = new HashMap<>();

	static {
		forcedInterpMap.put(War3ID.fromString("KLAV"), 0);
		forcedInterpMap.put(War3ID.fromString("KATV"), 0);
		forcedInterpMap.put(War3ID.fromString("KPEV"), 0);
		forcedInterpMap.put(War3ID.fromString("KP2V"), 0);
		forcedInterpMap.put(War3ID.fromString("KRVS"), 0);
	}

	public static Map<War3ID, float[]> defVals = new HashMap<>();

	static {
		// LAYS
		defVals.put(War3ID.fromString("KMTF"), new float[] { 0 });
		defVals.put(War3ID.fromString("KMTA"), new float[] { 1 });
		// TXAN
		defVals.put(War3ID.fromString("KTAT"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KTAR"), new float[] { 0, 0, 0, 1 });
		defVals.put(War3ID.fromString("KTAS"), new float[] { 1, 1, 1 });
		// GEOA
		defVals.put(War3ID.fromString("KGAO"), new float[] { 1 });
		defVals.put(War3ID.fromString("KGAC"), new float[] { 0, 0, 0 });
		// LITE
		defVals.put(War3ID.fromString("KLAS"), new float[] { 0 });
		defVals.put(War3ID.fromString("KLAE"), new float[] { 0 });
		defVals.put(War3ID.fromString("KLAC"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KLAI"), new float[] { 0 });
		defVals.put(War3ID.fromString("KLBI"), new float[] { 0 });
		defVals.put(War3ID.fromString("KLBC"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KLAV"), new float[] { 1 });
		// ATCH
		defVals.put(War3ID.fromString("KATV"), new float[] { 1 });
		// PREM
		defVals.put(War3ID.fromString("KPEE"), new float[] { 0 });
		defVals.put(War3ID.fromString("KPEG"), new float[] { 0 });
		defVals.put(War3ID.fromString("KPLN"), new float[] { 0 });
		defVals.put(War3ID.fromString("KPLT"), new float[] { 0 });
		defVals.put(War3ID.fromString("KPEL"), new float[] { 0 });
		defVals.put(War3ID.fromString("KPES"), new float[] { 0 });
		defVals.put(War3ID.fromString("KPEV"), new float[] { 1 });
		// PRE2
		defVals.put(War3ID.fromString("KP2S"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2R"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2L"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2G"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2E"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2N"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2W"), new float[] { 0 });
		defVals.put(War3ID.fromString("KP2V"), new float[] { 1 });
		// RIBB
		defVals.put(War3ID.fromString("KRHA"), new float[] { 0 });
		defVals.put(War3ID.fromString("KRHB"), new float[] { 0 });
		defVals.put(War3ID.fromString("KRAL"), new float[] { 1 });
		defVals.put(War3ID.fromString("KRCO"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KRTX"), new float[] { 0 });
		defVals.put(War3ID.fromString("KRVS"), new float[] { 1 });
		// CAMS
		defVals.put(War3ID.fromString("KCTR"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KTTR"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KCRL"), new float[] { 0 });
		// NODE
		defVals.put(War3ID.fromString("KGTR"), new float[] { 0, 0, 0 });
		defVals.put(War3ID.fromString("KGRT"), new float[] { 0, 0, 0, 1 });
		defVals.put(War3ID.fromString("KGSC"), new float[] { 1, 1, 1 });

	}

	public Sd(final MdxModel model, final MdlxTimeline<TYPE> timeline, final SdArrayDescriptor<TYPE> arrayDescriptor) {
		final List<Long> globalSequences = model.getGlobalSequences();
		final int globalSequenceId = timeline.getGlobalSequenceId();
		final Integer forcedInterp = forcedInterpMap.get(timeline.getName());

		this.model = model;
		this.name = timeline.getName();
		this.defval = convertDefaultValue(defVals.get(timeline.getName()));
		this.globalSequence = null;
		this.sequences = new ArrayList<>();

		// Allow to force an interpolation type.
		// The game seems to do this with visibility tracks, where the type is
		// forced to None.
		// It came up as a bug report by a user who used the wrong interpolation
		// type.
		this.interpolationType = forcedInterp != null ? forcedInterp : timeline.getInterpolationType().ordinal();

		if ((globalSequenceId != -1) && (globalSequences.size() > 0)) {
			this.globalSequence = new SdSequence<TYPE>(this, 0, globalSequences.get(globalSequenceId).longValue(),
					timeline, true, arrayDescriptor);
		}
		else {
			for (final Sequence sequence : model.getSequences()) {
				final long[] interval = sequence.getInterval();

				this.sequences
						.add(new SdSequence<TYPE>(this, interval[0], interval[1], timeline, false, arrayDescriptor));
			}
		}
	}

	public int getValue(final TYPE out, final int sequence, final int frame, final int counter) {
		if (this.globalSequence != null) {
			return this.globalSequence.getValue(out,
					this.globalSequence.end == 0 ? 0 : counter % this.globalSequence.end);
		}
		else if ((sequence != -1) && (this.sequences.size() > sequence)) {
			return this.sequences.get(sequence).getValue(out, frame);
		}
		else {
			this.copy(out, this.defval);

			return -1;
		}
	}

	public boolean isVariant(final int sequence) {
		if (this.globalSequence != null) {
			return !this.globalSequence.constant;
		}
		else {
			return !this.sequences.get(sequence).constant;
		}
	}

	protected abstract TYPE convertDefaultValue(float[] defaultValue);

	protected abstract void copy(TYPE out, TYPE value);

	protected abstract void interpolate(TYPE out, TYPE[] values, TYPE[] inTans, TYPE[] outTans, int start, int end,
			float t);
}
