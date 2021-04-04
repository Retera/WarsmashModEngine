package com.etheller.warsmash.viewer5.handlers.mdx;

public enum SequenceLoopMode {
	NEVER_LOOP,
	MODEL_LOOP,
	ALWAYS_LOOP,
	NEVER_LOOP_AND_HIDE_WHEN_DONE, // used by spawned effects
	LOOP_TO_NEXT_ANIMATION; // used by the Arthas vs Illidan tech demo
}
