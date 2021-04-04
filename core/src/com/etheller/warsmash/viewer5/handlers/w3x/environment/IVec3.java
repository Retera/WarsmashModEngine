package com.etheller.warsmash.viewer5.handlers.w3x.environment;

public class IVec3 {
	public int x;
	public int y;
	public int z;

	public IVec3(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setY(final int y) {
		this.y = y;
	}

	public void setZ(final int z) {
		this.z = z;
	}

}
