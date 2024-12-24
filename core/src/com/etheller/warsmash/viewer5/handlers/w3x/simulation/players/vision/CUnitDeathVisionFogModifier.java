package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CUnitDeathVisionFogModifier extends CFogModifier {
	private CUnit unit;
	private int endTurnTick;

	public CUnitDeathVisionFogModifier(final CUnit unit) {
		this.unit = unit;
	}

	@Override
	public void onAdd(final CSimulation game, final CPlayer player) {
		this.endTurnTick = (int) Math
				.floor(game.getGameTurnTick() + (DYING_UNIT_VISION_DURATION / WarsmashConstants.SIMULATION_STEP_TIME));
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar) {
		if (DYING_UNIT_VISION_RADIUS > 0) {
			final boolean flying = this.unit.getMovementType() == MovementType.FLY;
			final float myX = this.unit.getX();
			final float myY = this.unit.getY();
			final int myZ = flying ? Integer.MAX_VALUE : game.getTerrainHeight(myX, myY);
			fogOfWar.setVisible(game.getPathingGrid().getFogOfWarIndexX(myX),
					game.getPathingGrid().getFogOfWarIndexY(myY), CFogState.VISIBLE);

			int myXi = game.getPathingGrid().getFogOfWarIndexX(myX);
			int myYi = game.getPathingGrid().getFogOfWarIndexY(myY);
			int maxXi = game.getPathingGrid().getFogOfWarIndexX(myX + DYING_UNIT_VISION_RADIUS);
			int maxYi = game.getPathingGrid().getFogOfWarIndexY(myY + DYING_UNIT_VISION_RADIUS);
			for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
				if (a * a <= DYING_UNIT_VISION_RADIUS_SQ) {
					fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi, myYi - a, myXi, myYi - a + 1, myX,
							myY - a * CPlayerFogOfWar.GRID_STEP, myX, myY - (a - 1) * CPlayerFogOfWar.GRID_STEP, myZ);
					fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi, myYi + a, myXi, myYi + a - 1, myX,
							myY + a * CPlayerFogOfWar.GRID_STEP, myX, myY + (a - 1) * CPlayerFogOfWar.GRID_STEP, myZ);
					fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi - a, myYi, myXi - a + 1, myYi,
							myX - a * CPlayerFogOfWar.GRID_STEP, myY, myX - (a - 1) * CPlayerFogOfWar.GRID_STEP, myY,
							myZ);
					fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi + a, myYi, myXi + a - 1, myYi,
							myX + a * CPlayerFogOfWar.GRID_STEP, myY, myX + (a - 1) * CPlayerFogOfWar.GRID_STEP, myY,
							myZ);
				}
			}

			for (int y = 1; y <= maxYi - myYi; y++) {
				for (int x = 1; x <= maxXi - myXi; x++) {
					if (x * x + y * y <= DYING_UNIT_VISION_RADIUS_SQ) {
						int xf = x * CPlayerFogOfWar.GRID_STEP;
						int yf = y * CPlayerFogOfWar.GRID_STEP;

						fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi - x, myYi - y, myXi - x + 1,
								myYi - y + 1, myX - xf, myY - yf, myX - xf + CPlayerFogOfWar.GRID_STEP,
								myY - yf + CPlayerFogOfWar.GRID_STEP, myZ);
						fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi - x, myYi + y, myXi - x + 1,
								myYi + y - 1, myX - xf, myY + yf, myX - xf + CPlayerFogOfWar.GRID_STEP,
								myY + yf - CPlayerFogOfWar.GRID_STEP, myZ);
						fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi + x, myYi - y, myXi + x - 1,
								myYi - y + 1, myX + xf, myY - yf, myX + xf - CPlayerFogOfWar.GRID_STEP,
								myY - yf + CPlayerFogOfWar.GRID_STEP, myZ);
						fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi + x, myYi + y, myXi + x - 1,
								myYi + y - 1, myX + xf, myY + yf, myX + xf - CPlayerFogOfWar.GRID_STEP,
								myY + yf - CPlayerFogOfWar.GRID_STEP, myZ);
					}
				}
			}
		}
		if (game.getGameTurnTick() >= endTurnTick) {
			player.removeFogModifer(game, this);
		}
	}
}
