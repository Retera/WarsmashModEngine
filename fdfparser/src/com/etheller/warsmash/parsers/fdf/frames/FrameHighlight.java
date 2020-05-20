package com.etheller.warsmash.parsers.fdf.frames;

import com.etheller.warsmash.parsers.fdf.datamodel.HighlightAlphaMode;
import com.etheller.warsmash.parsers.fdf.datamodel.HighlightType;
import com.etheller.warsmash.parsers.fdf.frames.base.Frame;

public class FrameHighlight extends Frame {
	private HighlightType type;
	private String alphaFile;
	private HighlightAlphaMode alphaMode;

	public HighlightType getType() {
		return this.type;
	}

	public String getAlphaFile() {
		return this.alphaFile;
	}

	public HighlightAlphaMode getAlphaMode() {
		return this.alphaMode;
	}

	public void setType(final HighlightType type) {
		this.type = type;
	}

	public void setAlphaFile(final String alphaFile) {
		this.alphaFile = alphaFile;
	}

	public void setAlphaMode(final HighlightAlphaMode alphaMode) {
		this.alphaMode = alphaMode;
	}
}
