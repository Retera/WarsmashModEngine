package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import java.nio.ByteBuffer;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CFogMaskSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CPlayerFogOfWarEmpty implements CPlayerFogOfWarInterface {
	private final int width;
	private final int height;
	private final ByteBuffer fogOfWarBuffer;
	private final ByteBuffer detectionBuffer;

	public CPlayerFogOfWarEmpty() {
		this.width = 1;
		this.height = 1;
		this.fogOfWarBuffer = ByteBuffer.allocateDirect(1);
		this.detectionBuffer = ByteBuffer.allocateDirect(1);
		this.fogOfWarBuffer.clear();
		while (this.fogOfWarBuffer.hasRemaining()) {
			this.fogOfWarBuffer.put((byte) 0);
		}
		this.fogOfWarBuffer.clear();
		this.detectionBuffer.clear();
		while (this.detectionBuffer.hasRemaining()) {
			this.detectionBuffer.put((byte) 0);
		}
		this.detectionBuffer.clear();
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public ByteBuffer getFogOfWarBuffer() {
		return this.fogOfWarBuffer;
	}

	private byte getState(final CFogMaskSettings fogMaskSettings, final PathingGrid pathingGrid, final float x,
			final float y) {
		return 0;
	}

	private byte getState(final CFogMaskSettings fogMaskSettings, final int indexX, final int indexY) {
		return 0;
	}

	private byte getDetectionState(final int indexX, final int indexY) {
		return 0;
	}

	public CFogState getFogState(final CFogMaskSettings fogMaskSettings, final PathingGrid pathingGrid, final float x,
			final float y) {
		return CFogState.VISIBLE;
	}

	@Override
	public CFogState getFogState(final CFogMaskSettings fogMaskSettings, final int indexX, final int indexY) {
		return CFogState.VISIBLE;
	}

	@Override
	public boolean isVisible(final CFogMaskSettings fogMaskSettings, final PathingGrid pathingGrid, final float x,
			final float y) {
		return true;
	}

	@Override
	public boolean isVisible(final CFogMaskSettings fogMaskSettings, final int indexX, final int indexY) {
		return true;
	}

	@Override
	public boolean isDetecting(final CFogMaskSettings fogMaskSettings, final PathingGrid pathingGrid, final float x,
			final float y, final byte invisLevels) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		return isVisible(fogMaskSettings, indexX, indexY)
				&& ((getDetectionState(indexX, indexY) & invisLevels) == invisLevels);
	}

	public boolean isDetecting(final CFogMaskSettings fogMaskSettings, final int indexX, final int indexY,
			final byte invisLevels) {
		return isVisible(fogMaskSettings, indexX, indexY)
				&& ((getDetectionState(indexX, indexY) & invisLevels) == invisLevels);
	}

	public void setVisible(final PathingGrid pathingGrid, final float x, final float y, final CFogState fogOfWarState) {
	}

	@Override
	public void setVisible(final int indexX, final int indexY, final CFogState fogOfWarState) {
	}

	@Override
	public void setFogStateRect(final PathingGrid pathingGrid, final Rectangle rectangle,
			final CFogState fogOfWarState) {
	}

	public void setDetecting(final PathingGrid pathingGrid, final float x, final float y, final CFogState fogOfWarState,
			final byte detectLevels) {
		final int indexX = pathingGrid.getFogOfWarIndexX(x);
		final int indexY = pathingGrid.getFogOfWarIndexY(y);
		setDetecting(indexX, indexY, fogOfWarState, detectLevels);
	}

	@Override
	public void setDetecting(final int indexX, final int indexY, final CFogState fogOfWarState,
			final byte detectLevels) {
		final int writeIndex = (indexY * getWidth()) + indexX;
		if ((writeIndex >= 0) && (writeIndex < this.fogOfWarBuffer.capacity())) {
			this.fogOfWarBuffer.put(writeIndex, fogOfWarState.getMask());
			this.detectionBuffer.put(writeIndex, (byte) (this.detectionBuffer.get(writeIndex) | detectLevels));
		}
	}

	@Override
	public void checkCardinalVision(final CSimulation game, final PathingGrid pathingGrid, final boolean flying,
			final int dx, final int dy, final int dxp, final int dyp, final float dxf, final float dyf,
			final float dxfp, final float dyfp, final int myZ) {
	}

	@Override
	public void checkCardinalVision(final CSimulation game, final PathingGrid pathingGrid, final byte detections,
			final boolean flying, final int dx, final int dy, final int dxp, final int dyp, final float dxf,
			final float dyf, final float dxfp, final float dyfp, final int myZ) {
		if ((flying || game.isTerrainWater(dxf, dyf) || (myZ > game.getTerrainHeight(dxf, dyf))
				|| (!game.isTerrainRomp(dxf, dyf) && (myZ == game.getTerrainHeight(dxf, dyf))))
				&& (flying || !pathingGrid.isBlockVision(dxfp, dyfp)) && this.isVisible(game, dxp, dyp)) {
			this.setDetecting(dx, dy, CFogState.VISIBLE, detections);
		}
	}

	@Override
	public void checkDiagonalVision(final CSimulation game, final PathingGrid pathingGrid, final boolean flying,
			final int x, final int y, final int dx, final int dy, final int dxp, final int dyp, final float dxf,
			final float dyf, final float dxfp, final float dyfp, final int myZ) {
	}

	@Override
	public void checkDiagonalVision(final CSimulation game, final PathingGrid pathingGrid, final byte detections,
			final boolean flying, final int x, final int y, final int dx, final int dy, final int dxp, final int dyp,
			final float dxf, final float dyf, final float dxfp, final float dyfp, final int myZ) {
		if ((flying || game.isTerrainWater(dxf, dyf) || (myZ > game.getTerrainHeight(dxf, dyf))
				|| (!game.isTerrainRomp(dxf, dyf) && (myZ == game.getTerrainHeight(dxf, dyf))))
				&& (flying || !pathingGrid.isBlockVision(dxfp, dyfp)) && this.isVisible(game, dxp, dyp)
				&& ((x == y)
						|| ((x > y) && this.isVisible(game, dxp, dy)
								&& (flying || !pathingGrid.isBlockVision(dxfp, dyf)))
						|| ((x < y) && this.isVisible(game, dx, dyp)
								&& (flying || !pathingGrid.isBlockVision(dxf, dyfp))))) {
			this.setDetecting(dx, dy, CFogState.VISIBLE, detections);
		}
	}

	@Override
	public void convertVisibleToFogged() {
	}

	@Override
	public void setFogStateRadius(final PathingGrid pathingGrid, final float myX, final float myY, final float radius,
			final CFogState state) {
	}
}
