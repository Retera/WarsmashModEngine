package com.etheller.warsmash.parsers.mdlx.timeline;

public final class UInt32Timeline extends Timeline {

	@Override
	protected KeyFrame newKeyFrame() {
		return new UInt32KeyFrame();
	}

	@Override
	protected int size() {
		return 1;
	}

}
