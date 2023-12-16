package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CCircleFogModifier extends CFogModifier {
	private final byte state;
	private boolean enabled = true;
	private float myX;
	private float myY;
	private float radius;

	public CCircleFogModifier(final CFogState fogState, final float radius, final float x, final float y) {
		switch (fogState) {
		case FOGGED:
			state = 127;
			break;
		case MASKED:
			state = -128;
			break;
		case VISIBLE:
		default:
			state = 0;
			break;
		}
		this.radius = radius;
		this.myX = x;
		this.myY = y;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar) {
		if (!this.enabled || this.radius <= 0) {
			return;
		}
		final float radSq = this.radius * this.radius;
		fogOfWar.setState(pathingGrid.getFogOfWarIndexX(this.myX), pathingGrid.getFogOfWarIndexY(this.myY), state);

		for (int y = 0; y <= (int) Math.floor(this.radius); y += 128) {
			for (int x = 0; x <= (int) Math.floor(this.radius); x += 128) {
				float distance = x * x + y * y;
				if (distance <= radSq) {
					fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX - x),
							pathingGrid.getFogOfWarIndexY(myY - y), (byte) 0);
					fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX - x),
							pathingGrid.getFogOfWarIndexY(myY + y), (byte) 0);
					fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX + x),
							pathingGrid.getFogOfWarIndexY(myY - y), (byte) 0);
					fogOfWar.setState(pathingGrid.getFogOfWarIndexX(myX + x),
							pathingGrid.getFogOfWarIndexY(myY + y), (byte) 0);
				}
			}
		}
	}
}
