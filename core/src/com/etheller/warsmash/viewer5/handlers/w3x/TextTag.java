package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.etheller.interpreter.ast.util.CHandle;

public class TextTag implements CHandle {
	private final Vector3 position;
	private final Vector2 velocity;
	private String text;
	private final Color color;
	private float lifetimeDuration;
	private float fadeStart;
	private float fontHeight;
	private final int handleId;
	private final Vector2 screenCoordTravelOffset;
	private float lifetime = 0;
	private boolean suspended;
	private boolean visible;
	private boolean permanent;
	private boolean centered;

	public TextTag(final Vector3 position, final Vector2 velocity, final String text, final Color color,
			final float lifetimeDuration, final float fadeStart, final float fontHeight, final int handleId) {
		this.position = position;
		this.velocity = velocity;
		this.text = text;
		this.color = new Color(color);
		this.lifetimeDuration = lifetimeDuration;
		this.fadeStart = fadeStart;
		this.fontHeight = fontHeight;
		this.handleId = handleId;
		this.screenCoordTravelOffset = new Vector2(0, 0);
		this.visible = true;
		this.position.z += 10f;
	}

	public boolean update(final float deltaTime) {
		if (this.suspended) {
			return false;
		}
		this.screenCoordTravelOffset.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
		this.lifetime += deltaTime;
		final float fadeStart = getFadeStart();
		final float remainingLife = getRemainingLife();
		final float lifetimeDuration = getLifetimeDuration();
		final float fadingSeconds = lifetimeDuration - fadeStart;
		if (this.permanent) {
			return false;
		}
		if (remainingLife <= fadingSeconds) {
			this.color.a = remainingLife / fadingSeconds;
		}
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

	public void setText(final String text) {
		this.text = text;
	}

	public Vector2 getScreenCoordTravelOffset() {
		return this.screenCoordTravelOffset;
	}

	public float getFadeStart() {
		return this.fadeStart;
	}

	public void setFadeStart(final float fadeStart) {
		this.fadeStart = fadeStart;
	}

	public float getFontHeight() {
		return this.fontHeight;
	}

	public void setFontHeight(final float fontHeight) {
		this.fontHeight = fontHeight;
	}

	public void setLifetime(final float lifetime) {
		this.lifetime = lifetime;
	}

	public float getLifetimeDuration() {
		return this.lifetimeDuration;
	}

	public void setLifetimeDuration(final float lifetimeDuration) {
		this.lifetimeDuration = lifetimeDuration;
	}

	public boolean isSuspended() {
		return this.suspended;
	}

	public void setSuspended(final boolean suspended) {
		this.suspended = suspended;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

	public void setPosition(final float x, final float y, final float z) {
		this.position.set(x, y, z);
		this.position.z += 10f;
	}

	public void setColor(final int r, final int g, final int b, final float alpha) {
		this.color.r = r / 255.0f;
		this.color.g = g / 255.0f;
		this.color.b = b / 255.0f;
		this.color.a = alpha / 255f;
	}

	public void setVelocity(final float vx, final float vy) {
		this.velocity.set(vx, vy);
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public boolean isPermanent() {
		return this.permanent;
	}

	public void setPermanent(final boolean permanent) {
		this.permanent = permanent;
	}

	public void setCentered(final boolean centered) {
		this.centered = centered;
	}

	public boolean isCentered() {
		return this.centered;
	}
}
