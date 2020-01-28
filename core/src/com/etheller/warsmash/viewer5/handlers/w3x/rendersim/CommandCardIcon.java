package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.graphics.Texture;

public class CommandCardIcon {
	private final int x;
	private final int y;
	private final Texture texture;
	private final int orderId;

	public CommandCardIcon(final int x, final int y, final Texture texture, final int orderId) {
		this.x = x;
		this.y = y;
		this.texture = texture;
		this.orderId = orderId;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public Texture getTexture() {
		return this.texture;
	}

	public int getOrderId() {
		return this.orderId;
	}
}
