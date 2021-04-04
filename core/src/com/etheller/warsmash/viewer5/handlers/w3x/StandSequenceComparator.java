package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.Comparator;

public class StandSequenceComparator implements Comparator<IndexedSequence> {
	@Override
	public int compare(final IndexedSequence a, final IndexedSequence b) {
		return (int) Math.signum(b.sequence.getRarity() - a.sequence.getRarity());
	}

}
