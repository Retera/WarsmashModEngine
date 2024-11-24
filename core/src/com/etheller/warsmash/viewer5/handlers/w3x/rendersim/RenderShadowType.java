package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

public class RenderShadowType {
	private final String texture;
	private final float x;
	private final float y;
	private final float width;
	private final float height;

	public RenderShadowType(String texture, float x, float y, float width, float height) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String getTexture() {
		return this.texture;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}
}
