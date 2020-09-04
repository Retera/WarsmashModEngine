package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public abstract class CWidget {
	private final int handleId;
	private float x;
	private float y;
	private float life;

	public CWidget(final int handleId, final float x, final float y, final float life) {
		this.handleId = handleId;
		this.x = x;
		this.y = y;
		this.life = life;
	}

	public int getHandleId() {
		return this.handleId;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getLife() {
		return this.life;
	}

	protected void setX(final float x) {
		this.x = x;
	}

	protected void setY(final float y) {
		this.y = y;
	}

	public void setLife(final float life) {
		this.life = life;
	}

	public void damage(final CUnit source, final int damage) {
		this.life -= damage;
	}

	public abstract float getFlyHeight();

	public abstract float getImpactZ();

}
