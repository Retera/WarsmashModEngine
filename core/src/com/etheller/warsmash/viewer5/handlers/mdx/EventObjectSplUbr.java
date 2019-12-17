package com.etheller.warsmash.viewer5.handlers.mdx;

import com.etheller.warsmash.viewer5.EmittedObject;

public class EventObjectSplUbr<EMITTER extends MdxEmitter<?, ?, ?>> extends EmittedObject<MdxComplexInstance, EMITTER> {
	private final float[] vertices = new float[12];

	@Override
	protected void bind(final int flags) {

	}
}
