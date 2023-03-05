package com.etheller.warsmash.viewer5.handlers.mdx;

public interface SequencedCharacterNode {
	int getCharacterNodeSequence();

	int getCharacterNodeFrame();

	boolean isVisible();

	boolean wasDirty();
}
