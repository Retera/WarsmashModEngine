package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import java.nio.ByteBuffer;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CPlayerFogOfWar {
	public static final int PATHING_RATIO = 4;
	public static final int GRID_STEP = PATHING_RATIO * 32;
	private final int width;
	private final int height;
	private final ByteBuffer fogOfWarBuffer;
	private final ByteBuffer detectionBuffer;

	public CPlayerFogOfWar(final PathingGrid pathingGrid) {
		width = (pathingGrid.getWidth() / PATHING_RATIO) + 1;
		height = (pathingGrid.getHeight() / PATHING_RATIO) + 1;
		final int fogOfWarBufferLen = width * height;
		this.fogOfWarBuffer = ByteBuffer.allocateDirect(fogOfWarBufferLen);
		this.detectionBuffer = ByteBuffer.allocateDirect(fogOfWarBufferLen);
		fogOfWarBuffer.clear();
		while (fogOfWarBuffer.hasRemaining()) {
			fogOfWarBuffer.put((byte) -1);
		}
		fogOfWarBuffer.clear();
		detectionBuffer.clear();
		while (detectionBuffer.hasRemaining()) {
			detectionBuffer.put((byte) 0);
		}
		detectionBuffer.clear();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ByteBuffer getFogOfWarBuffer() {
		return fogOfWarBuffer;
	}

	private byte getState(final PathingGrid pathingGrid, final float x, final float y) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		return getState(indexX, indexY);
	}

	private byte getState(final int indexX, final int indexY) {
		final int index = (indexY * getWidth()) + indexX;
		if ((index >= 0) && (index < fogOfWarBuffer.capacity())) {
			return fogOfWarBuffer.get(index);
		}
		return 0;
	}

	private byte getDetectionState(final int indexX, final int indexY) {
		final int index = (indexY * getWidth()) + indexX;
		if ((index >= 0) && (index < detectionBuffer.capacity())) {
			return detectionBuffer.get(index);
		}
		return 0;
	}

	public CFogState getFogState(final PathingGrid pathingGrid, final float x, final float y) {
		return CFogState.getByMask(getState(pathingGrid, x, y));
	}

	public CFogState getFogState(final int indexX, final int indexY) {
		return CFogState.getByMask(getState(indexX,indexY));
	}

	public boolean isVisible(final PathingGrid pathingGrid, final float x, final float y) {
		return getState(pathingGrid, x, y)==0;
	}
	
	public boolean isVisible(final int indexX, final int indexY) {
		return getState(indexX, indexY)==0;
	}

	public boolean isDetecting(PathingGrid pathingGrid, float x, float y, byte invisLevels) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		return getState(indexX,indexY)==0 && (getDetectionState(indexX,indexY) & invisLevels) == invisLevels;
	}

	public boolean isDetecting(final int indexX, final int indexY, byte invisLevels) {
		return getState(indexX,indexY)==0 && (getDetectionState(indexX,indexY) & invisLevels) == invisLevels;
	}

	private void setState(final PathingGrid pathingGrid, final float x, final float y, final byte fogOfWarState) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		setState(indexX, indexY, fogOfWarState);
	}

	private void setState(final int indexX, final int indexY, final byte fogOfWarState) {
		final int writeIndex = (indexY * getWidth()) + indexX;
		if ((writeIndex >= 0) && (writeIndex < fogOfWarBuffer.capacity())) {
			fogOfWarBuffer.put(writeIndex, fogOfWarState);
		}
	}

	public void setVisible(final PathingGrid pathingGrid, final float x, final float y, final CFogState fogOfWarState) {
		setState(pathingGrid, x, y, fogOfWarState.getMask());
	}

	public void setVisible(final int indexX, final int indexY, final CFogState fogOfWarState) {
		setState(indexX, indexY, fogOfWarState.getMask());
	}

	public void setDetecting(final PathingGrid pathingGrid, final float x, final float y, final CFogState fogOfWarState, byte detectLevels) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		setDetecting(indexX, indexY, fogOfWarState, detectLevels);
	}

	public void setDetecting(final int indexX, final int indexY, final CFogState fogOfWarState, byte detectLevels) {
		final int writeIndex = (indexY * getWidth()) + indexX;
		if ((writeIndex >= 0) && (writeIndex < fogOfWarBuffer.capacity())) {
			fogOfWarBuffer.put(writeIndex, fogOfWarState.getMask());
			detectionBuffer.put(writeIndex, (byte) (detectionBuffer.get(writeIndex) | detectLevels));
		}
	}

	public void convertVisibleToFogged() {
		for (int i = 0; i < fogOfWarBuffer.capacity(); i++) {
			if (fogOfWarBuffer.get(i) == 0) {
				fogOfWarBuffer.put(i, (byte) 127);
			}
			detectionBuffer.put(i, (byte)0);
		}
	}
}
