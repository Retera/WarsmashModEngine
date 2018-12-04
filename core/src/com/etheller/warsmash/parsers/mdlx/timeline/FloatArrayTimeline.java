package com.etheller.warsmash.parsers.mdlx.timeline;

public final class FloatArrayTimeline extends Timeline {
	private final int arraySize;

	public FloatArrayTimeline(final int arraySize) {
		this.arraySize = arraySize;
	}

	@Override
	protected KeyFrame newKeyFrame() {
		return new FloatArrayKeyFrame(this.arraySize);
	}

	@Override
	protected int size() {
		return this.arraySize;
	}

}
