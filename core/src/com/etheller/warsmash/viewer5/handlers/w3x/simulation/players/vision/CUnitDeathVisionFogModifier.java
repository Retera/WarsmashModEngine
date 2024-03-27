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
				int distance = a * a;

				if (distance <= DYING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX, myY - (a - 1) * CPlayerFogOfWar.GRID_STEP))
						&& fogOfWar.isVisible(myXi, myYi - a + 1)
						&& (flying || game.isTerrainWater(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
								|| myZ > game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
								|| (!game.isTerrainRomp(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
										&& myZ == game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)))) {
					fogOfWar.setVisible(myXi, myYi - a, CFogState.VISIBLE);
				}
				if (distance <= DYING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX, myY + (a - 1) * CPlayerFogOfWar.GRID_STEP))
						&& fogOfWar.isVisible(myXi, myYi + a - 1)
						&& (flying || game.isTerrainWater(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
								|| myZ > game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
								|| (!game.isTerrainRomp(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
										&& myZ == game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)))) {
					fogOfWar.setVisible(myXi, myYi + a, CFogState.VISIBLE);
				}
				if (distance <= DYING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX - (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
						&& fogOfWar.isVisible(myXi - a + 1, myYi)
						&& (flying || game.isTerrainWater(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
								|| myZ > game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
								|| (!game.isTerrainRomp(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
										&& myZ == game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)))) {
					fogOfWar.setVisible(myXi - a, myYi, CFogState.VISIBLE);
				}
				if (distance <= DYING_UNIT_VISION_RADIUS_SQ
						&& (flying || !pathingGrid.isBlockVision(myX + (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
						&& fogOfWar.isVisible(myXi + a - 1, myYi)
						&& (flying || game.isTerrainWater(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
								|| myZ > game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
								|| (!game.isTerrainRomp(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
										&& myZ == game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)))) {
					fogOfWar.setVisible(myXi + a, myYi, CFogState.VISIBLE);
				}
			}

			for (int y = 1; y <= maxYi - myYi; y++) {
				for (int x = 1; x <= maxXi - myXi; x++) {
					float distance = x * x + y * y;
					if (distance <= DYING_UNIT_VISION_RADIUS_SQ) {
						int xf = x * CPlayerFogOfWar.GRID_STEP;
						int yf = y * CPlayerFogOfWar.GRID_STEP;

						if ((flying || game.isTerrainWater(myX - xf, myY - yf)
								|| myZ > game.getTerrainHeight(myX - xf, myY - yf)
								|| (!game.isTerrainRomp(myX - xf, myY - yf)
										&& myZ == game.getTerrainHeight(myX - xf, myY - yf)))
								&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
										myY - yf + CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.isVisible(myXi - x + 1, myYi - y + 1)
								&& (x == y
										|| (x > y && fogOfWar.isVisible(myXi - x + 1, myYi - y)
												&& (flying || !pathingGrid
														.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP, myY - yf)))
										|| (x < y && fogOfWar.isVisible(myXi - x, myYi - y + 1)
												&& (flying || !pathingGrid.isBlockVision(myX - xf,
														myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setVisible(myXi - x, myYi - y, CFogState.VISIBLE);
						}
						if ((flying || game.isTerrainWater(myX - xf, myY + yf)
								|| myZ > game.getTerrainHeight(myX - xf, myY + yf)
								|| (!game.isTerrainRomp(myX - xf, myY + yf)
										&& myZ == game.getTerrainHeight(myX - xf, myY + yf)))
								&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
										myY + yf - CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.isVisible(myXi - x + 1, myYi + y - 1)
								&& (x == y
										|| (x > y && fogOfWar.isVisible(myXi - x + 1, myYi + y)
												&& (flying || !pathingGrid
														.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP, myY + yf)))
										|| (x < y && fogOfWar.isVisible(myXi - x, myYi + y - 1)
												&& (flying || !pathingGrid.isBlockVision(myX - xf,
														myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setVisible(myXi - x, myYi + y, CFogState.VISIBLE);
						}
						if ((flying || game.isTerrainWater(myX + xf, myY - yf)
								|| myZ > game.getTerrainHeight(myX + xf, myY - yf)
								|| (!game.isTerrainRomp(myX + xf, myY - yf)
										&& myZ == game.getTerrainHeight(myX + xf, myY - yf)))
								&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
										myY - yf + CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.isVisible(myXi + x - 1, myYi - y + 1)
								&& (x == y
										|| (x > y && fogOfWar.isVisible(myXi + x - 1, myYi - y)
												&& (flying || !pathingGrid
														.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP, myY - yf)))
										|| (x < y && fogOfWar.isVisible(myXi + x, myYi - y + 1)
												&& (flying || !pathingGrid.isBlockVision(myX + xf,
														myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setVisible(myXi + x, myYi - y, CFogState.VISIBLE);
						}
						if ((flying || game.isTerrainWater(myX + xf, myY + yf)
								|| myZ > game.getTerrainHeight(myX + xf, myY + yf)
								|| (!game.isTerrainRomp(myX + xf, myY + yf)
										&& myZ == game.getTerrainHeight(myX + xf, myY + yf)))
								&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
										myY + yf - CPlayerFogOfWar.GRID_STEP))
								&& fogOfWar.isVisible(myXi + x - 1, myYi + y - 1)
								&& (x == y
										|| (x > y && fogOfWar.isVisible(myXi + x - 1, myYi + y)
												&& (flying || !pathingGrid
														.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP, myY + yf)))
										|| (x < y && fogOfWar.isVisible(myXi + x, myYi + y - 1)
												&& (flying || !pathingGrid.isBlockVision(myX + xf,
														myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
							fogOfWar.setVisible(myXi + x, myYi + y, CFogState.VISIBLE);
						}
					}
				}
			}
		}
		if (game.getGameTurnTick() >= endTurnTick) {
			player.removeFogModifer(game, this);
		}
	}
}
