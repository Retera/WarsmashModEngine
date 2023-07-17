package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.graphics.Color;

public class TextTagConfig {
	private Color color;
	private float[] velocity;
	private float lifetime;
	private float fadeStart;

	public TextTagConfig(Color color, float[] velocity, float lifetime, float fadeStart) {
		this.color = color;
		this.velocity = velocity;
		this.lifetime = lifetime;
		this.fadeStart = fadeStart;
	}

	public Color getColor() {
		return color;
	}

	public float[] getVelocity() {
		return velocity;
	}

	public float getLifetime() {
		return lifetime;
	}

	public float getFadeStart() {
		return fadeStart;
	}
}
