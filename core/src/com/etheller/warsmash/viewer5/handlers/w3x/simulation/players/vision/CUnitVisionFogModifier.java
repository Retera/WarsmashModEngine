package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CUnitVisionFogModifier extends CFogModifier {
	private boolean enabled = true;
	private CUnit unit;
	private boolean alwaysNightVision;

	public CUnitVisionFogModifier(final CUnit unit, final boolean alwaysNightVision) {
		this.unit = unit;
		this.alwaysNightVision = alwaysNightVision;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar) {
		if (enabled && !this.unit.isDead() && !this.unit.isHidden()) {
			final float sightRadius = game.isDay() && !alwaysNightVision ? this.unit.getUnitType().getSightRadiusDay()
					: this.unit.getUnitType().getSightRadiusNight();
			if (sightRadius > 0) {
				final boolean flying = this.unit.getUnitType().getMovementType() == MovementType.FLY;
				final float radSq = sightRadius * sightRadius / (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
				final float myX = this.unit.getX();
				final float myY = this.unit.getY();
				final int myZ = flying ? Integer.MAX_VALUE : game.getTerrainHeight(myX, myY);
				fogOfWar.setState(game.getPathingGrid().getFogOfWarIndexX(myX),
						game.getPathingGrid().getFogOfWarIndexY(myY), (byte) 0);

				int myXi = game.getPathingGrid().getFogOfWarIndexX(myX);
				int myYi = game.getPathingGrid().getFogOfWarIndexY(myY);
				int maxXi = game.getPathingGrid().getFogOfWarIndexX(myX + sightRadius);
				int maxYi = game.getPathingGrid().getFogOfWarIndexY(myY + sightRadius);
				for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
					int distance = a * a;

					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX, myY - (a - 1) * CPlayerFogOfWar.GRID_STEP))
							&& fogOfWar.getState(myXi, myYi - a + 1) == 0
							&& (flying || game.isTerrainWater(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
									|| myZ > game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
									|| (!game.isTerrainRomp(myX, myY - a * CPlayerFogOfWar.GRID_STEP) && myZ == game
											.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)))) {
						fogOfWar.setState(myXi, myYi - a, (byte) 0);
					}
					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX, myY + (a - 1) * CPlayerFogOfWar.GRID_STEP))
							&& fogOfWar.getState(myXi, myYi + a - 1) == 0
							&& (flying || game.isTerrainWater(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
									|| myZ > game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
									|| (!game.isTerrainRomp(myX, myY + a * CPlayerFogOfWar.GRID_STEP) && myZ == game
											.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)))) {
						fogOfWar.setState(myXi, myYi + a, (byte) 0);
					}
					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX - (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
							&& fogOfWar.getState(myXi - a + 1, myYi) == 0
							&& (flying || game.isTerrainWater(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
									|| myZ > game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
									|| (!game.isTerrainRomp(myX - a * CPlayerFogOfWar.GRID_STEP, myY) && myZ == game
											.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)))) {
						fogOfWar.setState(myXi - a, myYi, (byte) 0);
					}
					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX + (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
							&& fogOfWar.getState(myXi + a - 1, myYi) == 0
							&& (flying || game.isTerrainWater(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
									|| myZ > game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
									|| (!game.isTerrainRomp(myX + a * CPlayerFogOfWar.GRID_STEP, myY) && myZ == game
											.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)))) {
						fogOfWar.setState(myXi + a, myYi, (byte) 0);
					}
				}

				for (int y = 1; y <= maxYi - myYi; y++) {
					for (int x = 1; x <= maxXi - myXi; x++) {
						float distance = x * x + y * y;
						if (distance <= radSq) {
							int xf = x * CPlayerFogOfWar.GRID_STEP;
							int yf = y * CPlayerFogOfWar.GRID_STEP;

							if ((flying || game.isTerrainWater(myX - xf, myY - yf)
									|| myZ > game.getTerrainHeight(myX - xf, myY - yf)
									|| (!game.isTerrainRomp(myX - xf, myY - yf)
											&& myZ == game.getTerrainHeight(myX - xf, myY - yf)))
									&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
											myY - yf + CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.getState(myXi - x + 1, myYi - y + 1) == 0
									&& (x == y
											|| (x > y && fogOfWar.getState(myXi - x + 1, myYi - y) == 0
													&& (flying || !pathingGrid.isBlockVision(
															myX - xf + CPlayerFogOfWar.GRID_STEP, myY - yf)))
											|| (x < y && fogOfWar.getState(myXi - x, myYi - y + 1) == 0
													&& (flying || !pathingGrid.isBlockVision(myX - xf,
															myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi - x, myYi - y, (byte) 0);
							}
							if ((flying || game.isTerrainWater(myX - xf, myY + yf)
									|| myZ > game.getTerrainHeight(myX - xf, myY + yf)
									|| (!game.isTerrainRomp(myX - xf, myY + yf)
											&& myZ == game.getTerrainHeight(myX - xf, myY + yf)))
									&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
											myY + yf - CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.getState(myXi - x + 1, myYi + y - 1) == 0
									&& (x == y
											|| (x > y && fogOfWar.getState(myXi - x + 1, myYi + y) == 0
													&& (flying || !pathingGrid.isBlockVision(
															myX - xf + CPlayerFogOfWar.GRID_STEP, myY + yf)))
											|| (x < y && fogOfWar.getState(myXi - x, myYi + y - 1) == 0
													&& (flying || !pathingGrid.isBlockVision(myX - xf,
															myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi - x, myYi + y, (byte) 0);
							}
							if ((flying || game.isTerrainWater(myX + xf, myY - yf)
									|| myZ > game.getTerrainHeight(myX + xf, myY - yf)
									|| (!game.isTerrainRomp(myX + xf, myY - yf)
											&& myZ == game.getTerrainHeight(myX + xf, myY - yf)))
									&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
											myY - yf + CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.getState(myXi + x - 1, myYi - y + 1) == 0
									&& (x == y
											|| (x > y && fogOfWar.getState(myXi + x - 1, myYi - y) == 0
													&& (flying || !pathingGrid.isBlockVision(
															myX + xf - CPlayerFogOfWar.GRID_STEP, myY - yf)))
											|| (x < y && fogOfWar.getState(myXi + x, myYi - y + 1) == 0
													&& (flying || !pathingGrid.isBlockVision(myX + xf,
															myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi + x, myYi - y, (byte) 0);
							}
							if ((flying || game.isTerrainWater(myX + xf, myY + yf)
									|| myZ > game.getTerrainHeight(myX + xf, myY + yf)
									|| (!game.isTerrainRomp(myX + xf, myY + yf)
											&& myZ == game.getTerrainHeight(myX + xf, myY + yf)))
									&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
											myY + yf - CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.getState(myXi + x - 1, myYi + y - 1) == 0
									&& (x == y
											|| (x > y && fogOfWar.getState(myXi + x - 1, myYi + y) == 0
													&& (flying || !pathingGrid.isBlockVision(
															myX + xf - CPlayerFogOfWar.GRID_STEP, myY + yf)))
											|| (x < y && fogOfWar.getState(myXi + x, myYi + y - 1) == 0
													&& (flying || !pathingGrid.isBlockVision(myX + xf,
															myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setState(myXi + x, myYi + y, (byte) 0);
							}
						}
					}
				}
			}
		}
	}
}
