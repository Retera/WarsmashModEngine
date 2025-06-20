package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import java.nio.ByteBuffer;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CFogMaskSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public interface CPlayerFogOfWarInterface {

	void convertVisibleToFogged();

	boolean isDetecting(CFogMaskSettings simulation, PathingGrid pathingGrid, float x, float y, byte invisLevels);

	boolean isVisible(CFogMaskSettings simulation, PathingGrid pathingGrid, float x, float y);

	CFogState getFogState(CFogMaskSettings game, int x, int y);

	void setVisible(int fogOfWarIndexX, int fogOfWarIndexY, CFogState state);

	boolean isVisible(CFogMaskSettings simulation, int fogOfWarIndexX, int fogOfWarIndexY);

	void setFogStateRadius(PathingGrid pathingGrid, float centerX, float centerY, float radius, CFogState whichState);

	void setFogStateRect(PathingGrid pathingGrid, Rectangle whichRect, CFogState whichState);

	ByteBuffer getFogOfWarBuffer();

	int getWidth();

	int getHeight();

	void setDetecting(int fogOfWarIndexX, int fogOfWarIndexY, CFogState visible, byte detections);

	void checkCardinalVision(final CSimulation game, final PathingGrid pathingGrid, final boolean flying, final int dx,
			final int dy, final int dxp, final int dyp, final float dxf, final float dyf, final float dxfp,
			final float dyfp, final int myZ);

	void checkCardinalVision(final CSimulation game, final PathingGrid pathingGrid, final byte detections,
			final boolean flying, final int dx, final int dy, final int dxp, final int dyp, final float dxf,
			final float dyf, final float dxfp, final float dyfp, final int myZ);

	void checkDiagonalVision(final CSimulation game, final PathingGrid pathingGrid, final boolean flying, final int x,
			final int y, final int dx, final int dy, final int dxp, final int dyp, final float dxf, final float dyf,
			final float dxfp, final float dyfp, final int myZ);

	void checkDiagonalVision(final CSimulation game, final PathingGrid pathingGrid, final byte detections,
			final boolean flying, final int x, final int y, final int dx, final int dy, final int dxp, final int dyp,
			final float dxf, final float dyf, final float dxfp, final float dyfp, final int myZ);

}
