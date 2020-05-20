package com.etheller.warsmash.parsers.fdf.datamodel;

public class ColorDefinition {
	private final float red, green, blue, alpha;

	public ColorDefinition(final float red, final float green, final float blue, final float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public float getRed() {
		return this.red;
	}

	public float getGreen() {
		return this.green;
	}

	public float getBlue() {
		return this.blue;
	}

	public float getAlpha() {
		return this.alpha;
	}
}
