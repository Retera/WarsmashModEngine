package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class TextTag {
	private final Vector3 position;
	private float screenCoordsZHeight;
	private final String text;
	private final Color color;
	private final float lifetimeDuration;
	private final float fadeStart;
	private float lifetime = 0;

	public TextTag(final Vector3 position, final String text, final Color color, final float lifetimeDuration, final float fadeStart) {
		this.position = position;
		this.text = text;
		this.color = color;
		this.lifetimeDuration = lifetimeDuration;
		this.fadeStart = fadeStart;
		position.z += 64f;
	}

	public boolean update(final float deltaTime) {
		this.screenCoordsZHeight += 60.0f * deltaTime;
		this.lifetime += deltaTime;
		return this.lifetime > this.lifetimeDuration;
	}

	public Vector3 getPosition() {
		return this.position;
	}

	public float getRemainingLife() {
		return this.lifetimeDuration - this.lifetime;
	}

	public Color getColor() {
		return this.color;
	}

	public String getText() {
		return this.text;
	}

	public float getScreenCoordsZHeight() {
		return this.screenCoordsZHeight;
	}

	public float getFadeStart() {
		return fadeStart;
	}

	public float getLifetimeDuration() {
		return lifetimeDuration;
	}
}
