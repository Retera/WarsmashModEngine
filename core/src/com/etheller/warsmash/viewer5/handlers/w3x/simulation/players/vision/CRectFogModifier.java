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
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar) {
		if (!this.enabled) {
			return;
		}
		fogOfWar.setFogStateRect(pathingGrid, this.area, this.state);
	}
}
