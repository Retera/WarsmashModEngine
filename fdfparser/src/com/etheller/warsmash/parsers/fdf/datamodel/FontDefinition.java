package com.etheller.warsmash.parsers.fdf.datamodel;

public class FontDefinition {
	private final String fontName;
	private final float fontSize;
	private final String extra;

	public FontDefinition(final String fontName, final float fontSize, final String extra) {
		this.fontName = fontName;
		this.fontSize = fontSize;
		this.extra = extra;
	}

	public String getFontName() {
		return this.fontName;
	}

	public float getFontSize() {
		return this.fontSize;
	}

	public String getExtra() {
		return this.extra;
	}
}
