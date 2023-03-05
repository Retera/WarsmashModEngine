package com.etheller.warsmash.viewer5.handlers.w3x.camera;

public final class CameraPanControls {
	public boolean down;
	public boolean up;
	public boolean left;
	public boolean right;
	public boolean insertDown;
	public boolean deleteDown;

	public boolean isAnyArrowPressed() {
		return this.down || this.up || this.left || this.right;
	}
}