package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.nio.ByteBuffer;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;

public class CPlayerFogOfWar {
	private final int width;
	private final int height;
	private final ByteBuffer fogOfWarBuffer;

	public CPlayerFogOfWar(final PathingGrid pathingGrid) {
		this.width = (pathingGrid.getWidth() / 8) + 1;
		this.height = (pathingGrid.getHeight() / 8) + 1;
		final int fogOfWarBufferLen = this.width * this.height;
		this.fogOfWarBuffer = ByteBuffer.allocateDirect(fogOfWarBufferLen);
		this.fogOfWarBuffer.clear();
		while (this.fogOfWarBuffer.hasRemaining()) {
			this.fogOfWarBuffer.put((byte) -1);
		}
		this.fogOfWarBuffer.clear();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public ByteBuffer getFogOfWarBuffer() {
		return this.fogOfWarBuffer;
	}

	public byte getState(final PathingGrid pathingGrid, final float x, final float y) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		return getState(indexX, indexY);
	}

	public byte getState(final int indexX, final int indexY) {
		final int index = (indexY * getWidth()) + indexX;
		if ((index >= 0) && (index < this.fogOfWarBuffer.capacity())) {
			return this.fogOfWarBuffer.get(index);
		}
		return 0;
	}

	public void setState(final PathingGrid pathingGrid, final float x, final float y, final byte fogOfWarState) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		setState(indexX, indexY, fogOfWarState);
	}

	public void setState(final int indexX, final int indexY, final byte fogOfWarState) {
		final int writeIndex = (indexY * getWidth()) + indexX;
		if ((writeIndex >= 0) && (writeIndex < this.fogOfWarBuffer.capacity())) {
			this.fogOfWarBuffer.put(writeIndex, fogOfWarState);
		}
	}

	public void convertVisibleToFogged() {
		for (int i = 0; i < this.fogOfWarBuffer.capacity(); i++) {
			if (this.fogOfWarBuffer.get(i) == 0) {
				this.fogOfWarBuffer.put(i, (byte) 127);
			}
		}
	}
}
