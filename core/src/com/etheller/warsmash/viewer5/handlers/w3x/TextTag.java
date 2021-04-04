package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class TextTag {
	private final Vector3 position;
	private float screenCoordsZHeight;
	private final String text;
	private final Color color;
	private float lifetime = 0;

	public TextTag(final Vector3 position, final String text, final Color color) {
		this.position = position;
		this.text = text;
		this.color = color;
		position.z += 64f;
	}

	public boolean update(final float deltaTime) {
		this.screenCoordsZHeight += 60.0f * deltaTime;
		this.lifetime += deltaTime;
		return this.lifetime > 2.5f;
	}

	public Vector3 getPosition() {
		return this.position;
	}

	public float getRemainingLife() {
		return 2.5f - this.lifetime;
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
}
