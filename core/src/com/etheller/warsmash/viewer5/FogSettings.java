package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.graphics.Color;

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
		return "FogSettings [style=" + style + ", color=" + color + ", density=" + density + ", start=" + start
				+ ", end=" + end + "]";
	}
}
