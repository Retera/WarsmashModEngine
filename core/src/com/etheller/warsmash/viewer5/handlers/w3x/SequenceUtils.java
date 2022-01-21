package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;

public class SequenceUtils {
	public static final EnumSet<SecondaryTag> EMPTY = EnumSet.noneOf(SecondaryTag.class);
	public static final EnumSet<SecondaryTag> READY = EnumSet.of(SecondaryTag.READY);
	public static final EnumSet<SecondaryTag> FLESH = EnumSet.of(SecondaryTag.FLESH);
	public static final EnumSet<SecondaryTag> TALK = EnumSet.of(SecondaryTag.TALK);
	public static final EnumSet<SecondaryTag> BONE = EnumSet.of(SecondaryTag.BONE);
	public static final EnumSet<SecondaryTag> HIT = EnumSet.of(SecondaryTag.HIT);
	public static final EnumSet<SecondaryTag> SPELL = EnumSet.of(SecondaryTag.SPELL);
	public static final EnumSet<SecondaryTag> WORK = EnumSet.of(SecondaryTag.WORK);
	public static final EnumSet<SecondaryTag> FAST = EnumSet.of(SecondaryTag.FAST);
	public static final EnumSet<SecondaryTag> ALTERNATE = EnumSet.of(SecondaryTag.ALTERNATE);

	private static final StandSequenceComparator STAND_SEQUENCE_COMPARATOR = new StandSequenceComparator();
	private static final SecondaryTagSequenceComparator SECONDARY_TAG_SEQUENCE_COMPARATOR = new SecondaryTagSequenceComparator(
			STAND_SEQUENCE_COMPARATOR);

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

	private static List<IndexedSequence> filterSequences(final PrimaryTag type, final EnumSet<SecondaryTag> tags,
			final List<Sequence> sequences) {
		final List<IndexedSequence> filtered = new ArrayList<>();

		for (int i = 0, l = sequences.size(); i < l; i++) {
			final Sequence sequence = sequences.get(i);
			if ((sequence.getPrimaryTags().contains(type) || (type == null))
					&& (sequence.getSecondaryTags().containsAll(tags)
							&& tags.containsAll(sequence.getSecondaryTags()))) {
				filtered.add(new IndexedSequence(sequence, i));
			}
		}

		return filtered;
	}

	public static IndexedSequence selectSequence(final String type, final List<Sequence> sequences) {
		final List<IndexedSequence> filtered = filterSequences(type, sequences);

		filtered.sort(STAND_SEQUENCE_COMPARATOR);

		int i = 0;
		final double randomRoll = Math.random() * 100;
		for (final int l = filtered.size(); i < l; i++) {
			final Sequence sequence = filtered.get(i).sequence;
			final float rarity = sequence.getRarity();

			if (rarity == 0) {
				break;
			}

			if (randomRoll < (10 - rarity)) {
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

	public static int matchCount(final EnumSet<AnimationTokens.SecondaryTag> goalTagSet,
			final EnumSet<AnimationTokens.SecondaryTag> tagsToTest) {
		int matches = 0;
		for (final AnimationTokens.SecondaryTag goalTag : goalTagSet) {
			if (tagsToTest.contains(goalTag)) {
				matches++;
			}
		}
		return matches;
	}

	public static IndexedSequence selectSequence(final AnimationTokens.PrimaryTag type,
			final EnumSet<AnimationTokens.SecondaryTag> tags, final List<Sequence> sequences,
			final boolean allowRarityVariations) {
		List<IndexedSequence> filtered = filterSequences(type, tags, sequences);
		final Comparator<IndexedSequence> sequenceComparator = STAND_SEQUENCE_COMPARATOR;

//		if (filtered.isEmpty() && !tags.isEmpty()) {
//			filtered = filterSequences(type, EMPTY, sequences);
//		}
		if (filtered.isEmpty()) {
			// find tags
			EnumSet<SecondaryTag> fallbackTags = null;
			int fallbackTagsMatchCount = 0;
			for (int i = 0, l = sequences.size(); i < l; i++) {
				final Sequence sequence = sequences.get(i);
				if (sequence.getPrimaryTags().contains(type) || (type == null)) {
					final int matchCount = matchCount(tags, sequence.getSecondaryTags());
					if (matchCount > fallbackTagsMatchCount) {
						fallbackTags = sequence.getSecondaryTags();
						fallbackTagsMatchCount = matchCount;
					}
				}
			}
			if (fallbackTags == null) {
				for (int i = 0, l = sequences.size(); i < l; i++) {
					final Sequence sequence = sequences.get(i);
					if (sequence.getPrimaryTags().contains(type) || (type == null)) {
						if ((fallbackTags == null) || (sequence.getSecondaryTags().size() < fallbackTags.size())
								|| ((sequence.getSecondaryTags().size() == fallbackTags.size())
										&& (SecondaryTagSequenceComparator.getTagsOrdinal(sequence.getSecondaryTags(),
												tags) > SecondaryTagSequenceComparator.getTagsOrdinal(fallbackTags,
														tags)))) {
							fallbackTags = sequence.getSecondaryTags();
						}
					}
				}
			}
			if (fallbackTags != null) {
				filtered = filterSequences(type, fallbackTags, sequences);
			}
		}

		filtered.sort(sequenceComparator);

		int i = 0;
		final double randomRoll = Math.random() * 100;
		for (final int l = filtered.size(); i < l; i++) {
			final Sequence sequence = filtered.get(i).sequence;
			final float rarity = sequence.getRarity();

			if (rarity == 0) {
				break;
			}

			if ((randomRoll < (10 - rarity)) && allowRarityVariations) {
				return filtered.get(i);
			}
		}

		final int sequencesLeft = filtered.size() - i;
		if (sequencesLeft <= 0) {
			if (filtered.size() > 0) {
				return filtered.get((int) Math.floor(Math.random() * filtered.size()));
			}
			return null; // new IndexedSequence(null, 0);
		}
		final int random = (int) (i + Math.floor(Math.random() * sequencesLeft));
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

	public static void randomDeathSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("death", sequences);

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

	public static void randomBirthSequence(final MdxComplexInstance target) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence("birth", sequences);

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

	public static Sequence randomSequence(final MdxComplexInstance target, final PrimaryTag animationName,
			final boolean allowRarityVariations) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence(animationName, null, sequences, allowRarityVariations);

		if (sequence != null) {
			target.setSequence(sequence.index);
			return sequence.sequence;
		}
		else {
			return null;
		}
	}

	public static Sequence randomSequence(final MdxComplexInstance target, final PrimaryTag animationName,
			final EnumSet<SecondaryTag> secondaryAnimationTags, final boolean allowRarityVariations) {
		final MdxModel model = (MdxModel) target.model;
		final List<Sequence> sequences = model.getSequences();
		final IndexedSequence sequence = selectSequence(animationName, secondaryAnimationTags, sequences,
				allowRarityVariations);

		if (sequence != null) {
			target.setSequence(sequence.index);
			return sequence.sequence;
		}
		else {
			if ((animationName == null) || (secondaryAnimationTags.size() != 1)
					|| !secondaryAnimationTags.contains(SecondaryTag.SPELL)) {
				return null;
			}
			else {
				return randomSequence(target, null, secondaryAnimationTags, allowRarityVariations);
			}
		}
	}

	public static Sequence randomSequence(final MdxComplexInstance target, final PrimaryTag animationName) {
		return randomSequence(target, animationName, EMPTY, false);
	}
}
