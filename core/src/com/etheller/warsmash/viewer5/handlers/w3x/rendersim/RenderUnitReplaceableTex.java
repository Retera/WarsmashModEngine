package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

public class RenderUnitReplaceableTex {
	private final int replaceableId;
	private final String path;

	public RenderUnitReplaceableTex(final int replaceableId, final String path) {
		this.replaceableId = replaceableId;
		this.path = path;
	}

	public int getReplaceableId() {
		return this.replaceableId;
	}

	public String getPath() {
		return this.path;
	}
}
