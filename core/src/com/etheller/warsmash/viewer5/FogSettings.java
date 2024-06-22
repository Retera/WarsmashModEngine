package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.graphics.Color;
import com.etheller.warsmash.units.Element;

public class FogSettings {
	public FogStyle style = FogStyle.NONE;
	public Color color = Color.BLACK;
	public float density;
	public float start;
	public float end;

	public void setStyleByIndex(final int styleValue) {
		this.style = ((styleValue >= 0) && (styleValue < FogStyle.values().length)) ? FogStyle.values()[styleValue]
				: FogStyle.NONE;
	}

	@Override
	public String toString() {
		return "FogSettings [style=" + this.style + ", color=" + this.color + ", density=" + this.density + ", start="
				+ this.start + ", end=" + this.end + "]";
	}

	public static FogSettings parse(final Element zFogElement, final int index) {
		final FogSettings newFogSettings = new FogSettings();
		final int styleValue = zFogElement.getFieldAsInteger("Style", index) + 1;
		newFogSettings.setStyleByIndex(styleValue);
		newFogSettings.start = zFogElement.getFieldAsFloat("Start", index);
		newFogSettings.end = zFogElement.getFieldAsFloat("End", index);
		newFogSettings.density = zFogElement.getFieldAsFloat("Density", index);
		final float a = zFogElement.getFieldAsFloat("Color", index * 4) / 255f;
		final float r = zFogElement.getFieldAsFloat("Color", 1 + (index * 4)) / 255f;
		final float g = zFogElement.getFieldAsFloat("Color", 2 + (index * 4)) / 255f;
		final float b = zFogElement.getFieldAsFloat("Color", 3 + (index * 4)) / 255f;
		newFogSettings.color = new Color(r, g, b, a);
		return newFogSettings;
	}
}
