package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CCircleFogModifier extends CFogModifier {
	private final CFogState state;
	private boolean enabled = true;
	private final float myX;
	private final float myY;
	private final float radius;

	public CCircleFogModifier(final CFogState fogState, final float radius, final float x, final float y) {
		this.state = fogState;
		this.radius = radius;
		this.myX = x;
		this.myY = y;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar) {
		if (!this.enabled || (this.radius <= 0)) {
			return;
		}
		fogOfWar.setFogStateRadius(pathingGrid, this.myX, this.myY, this.radius, this.state);
	}
}
