package com.etheller.warsmash.viewer5.handlers.w3x.environment;

public class WorldGrid {
	private final float[] centerOffset;
	private final float cellSize;

	public WorldGrid(final float[] centerOffset, final float cellSize) {
		this.centerOffset = centerOffset;
		this.cellSize = cellSize;
	}

	public int getCellX(final float x) {
		final double userCellSpaceX = StrictMath.floor((StrictMath.floor(x) - this.centerOffset[0]) / this.cellSize);
		final int cellX = (int) userCellSpaceX;
		return cellX;
	}

	public int getCellY(final float y) {
		final double userCellSpaceY = StrictMath.floor((StrictMath.floor(y) - this.centerOffset[1]) / this.cellSize);
		final int cellY = (int) userCellSpaceY;
		return cellY;
	}

	public float getWorldX(final int cellX) {
		return (cellX * this.cellSize) + this.centerOffset[0] + (this.cellSize / 2);
	}

	public float getWorldY(final int cellY) {
		return (cellY * this.cellSize) + this.centerOffset[1] + (this.cellSize / 2);
	}

	public float getCornerX(final int cellX) {
		return (cellX * this.cellSize) + this.centerOffset[0];
	}

	public float getCornerY(final int cellY) {
		return (cellY * this.cellSize) + this.centerOffset[1];
	}

}
