package com.etheller.warsmash.parsers.mdlx.timeline;

public final class FloatTimeline extends Timeline {

	@Override
	protected KeyFrame newKeyFrame() {
		return new FloatKeyFrame();
	}

	@Override
	protected int size() {
		return 1;
	}

}
