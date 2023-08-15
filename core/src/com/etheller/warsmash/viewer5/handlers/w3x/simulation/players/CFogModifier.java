package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CFogModifier {
	private final CFogState fogState;
	private final Rectangle area;
	private boolean enabled;

	public CFogModifier(final CFogState fogState, final Rectangle area) {
		this.fogState = fogState;
		this.area = area;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void update(final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar) {
		if (!this.enabled) {
			return;
		}
		final int xMin = pathingGrid.getFogOfWarIndexX((float) Math.floor(this.area.x));
		final int yMin = pathingGrid.getFogOfWarIndexY((float) Math.floor(this.area.y));
		final int xMax = pathingGrid.getFogOfWarIndexX((float) Math.ceil(this.area.x + this.area.width));
		final int yMax = pathingGrid.getFogOfWarIndexY((float) Math.ceil(this.area.y + this.area.height));
		byte state;
		switch (this.fogState) {
		case FOGGED:
			state = 127;
			break;
		case MASKED:
			state = -128;
			break;
		default:
		case VISIBLE:
			state = 0;
			break;
		}
		for (int i = xMin; i <= xMax; i += 1) {
			for (int j = yMin; j <= yMax; j += 1) {
				fogOfWar.setState(i, j, state);
			}
		}
	}
}
