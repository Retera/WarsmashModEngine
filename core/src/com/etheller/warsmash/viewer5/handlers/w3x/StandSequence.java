package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.mdlx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public class StandSequence {

	private static final StandSequenceComparator STAND_SEQUENCE_COMPARATOR = new StandSequenceComparator();

	public static List<IndexedSequence> filterSequences(final String type, final List<Sequence> sequences) {
		final List<IndexedSequence> filtered = new ArrayList<>();

		for (int i = 0, l = sequences.size(); i < l; i++) {
			final Sequence sequence = sequences.get(i);
			final String name = sequence.getName().split("-")[0].trim().toLowerCase();

			if (name.equals(type)) {
				filtered.add(new IndexedSequence(sequence, i));
			}
		}

		return filtered;
	}

	public static IndexedSequence selectSequence(final String type, final List<Sequence> sequences) {
		final List<IndexedSequence> filtered = filterSequences(type, sequences);

		filtered.sort(STAND_SEQUENCE_COMPARATOR);

		int i = 0;
		for (final int l = filtered.size(); i < l; i++) {
			final Sequence sequence = filtered.get(i).sequence;
			final float rarity = sequence.getRarity();

			if (rarity == 0) {
				break;
			}

			if ((Math.random() * 10) > rarity) {
				return filtered.get(i);
			}
		}

		final int sequencesLeft = filtered.size() - i;
		final int random = (int) (i + Math.floor(Math.random() * sequencesLeft));
		if (sequencesLeft <= 0) {
			return null; // new IndexedSequence(null, 0);
		}
		final IndexedSequence sequence = filtered.get(random);

		return sequence;
	}

	public static void randomStandSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("stand", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			target.setSequence(0);
		}
	}

	public static void randomWalkSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("walk", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}

	public static void randomPortraitSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("portrait", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}

	public static void randomPortraitTalkSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("portrait talk", sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomPortraitSequence(target);
		}
	}

	public static void randomSequence(final MdxComplexInstance target, final String sequenceName) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence(sequenceName, sequences);

		if (sequence != null) {
			target.setSequence(sequence.index);
		}
		else {
			randomStandSequence(target);
		}
	}
}
