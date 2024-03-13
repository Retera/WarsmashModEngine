package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CRectFogModifier extends CFogModifier {
	private final CFogState state;
	private final Rectangle area;
	private boolean enabled = true;

	public CRectFogModifier(final CFogState fogState, final Rectangle area) {
		this.state = fogState;
		this.area = area;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar) {
		if (!this.enabled) {
			return;
		}
		final int xMin = pathingGrid.getFogOfWarIndexX((float) Math.floor(this.area.x));
		final int yMin = pathingGrid.getFogOfWarIndexY((float) Math.floor(this.area.y));
		final int xMax = pathingGrid.getFogOfWarIndexX((float) Math.ceil(this.area.x + this.area.width));
		final int yMax = pathingGrid.getFogOfWarIndexY((float) Math.ceil(this.area.y + this.area.height));
		for (int i = xMin; i <= xMax; i += 1) {
			for (int j = yMin; j <= yMax; j += 1) {
				fogOfWar.setVisible(i, j, state);
			}
		}
	}
}
