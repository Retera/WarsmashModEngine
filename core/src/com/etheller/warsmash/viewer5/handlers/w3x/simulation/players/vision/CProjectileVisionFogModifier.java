package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class CProjectileVisionFogModifier extends CFogModifier {
	private boolean enabled = true;
	private CProjectile projectile;
	private float sightRadius;

	public CProjectileVisionFogModifier(final CProjectile projectile, final float sightRadius) {
		this.projectile = projectile;
		this.sightRadius = sightRadius;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWarInterface fogOfWar) {
		if (enabled && !this.projectile.isDone()) {
			if (sightRadius > 0) {
				final boolean flying = true;
				final float radSq = sightRadius * sightRadius / (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
				final float myX = this.projectile.getX();
				final float myY = this.projectile.getY();
				final int myZ = flying ? Integer.MAX_VALUE : game.getTerrainHeight(myX, myY);
				fogOfWar.setVisible(game.getPathingGrid().getFogOfWarIndexX(myX),
						game.getPathingGrid().getFogOfWarIndexY(myY), CFogState.VISIBLE);

				int myXi = game.getPathingGrid().getFogOfWarIndexX(myX);
				int myYi = game.getPathingGrid().getFogOfWarIndexY(myY);
				int maxXi = game.getPathingGrid().getFogOfWarIndexX(myX + sightRadius);
				int maxYi = game.getPathingGrid().getFogOfWarIndexY(myY + sightRadius);
				for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
					if (a * a <= radSq) {
						fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi, myYi - a, myXi, myYi - a + 1, myX,
								myY - a * CPlayerFogOfWar.GRID_STEP, myX, myY - (a - 1) * CPlayerFogOfWar.GRID_STEP,
								myZ);
						fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi, myYi + a, myXi, myYi + a - 1, myX,
								myY + a * CPlayerFogOfWar.GRID_STEP, myX, myY + (a - 1) * CPlayerFogOfWar.GRID_STEP,
								myZ);
						fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi - a, myYi, myXi - a + 1, myYi,
								myX - a * CPlayerFogOfWar.GRID_STEP, myY, myX - (a - 1) * CPlayerFogOfWar.GRID_STEP,
								myY, myZ);
						fogOfWar.checkCardinalVision(game, pathingGrid, flying, myXi + a, myYi, myXi + a - 1, myYi,
								myX + a * CPlayerFogOfWar.GRID_STEP, myY, myX + (a - 1) * CPlayerFogOfWar.GRID_STEP,
								myY, myZ);
					}
				}

				for (int y = 1; y <= maxYi - myYi; y++) {
					for (int x = 1; x <= maxXi - myXi; x++) {
						if (x * x + y * y <= radSq) {
							int xf = x * CPlayerFogOfWar.GRID_STEP;
							int yf = y * CPlayerFogOfWar.GRID_STEP;

							fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi - x, myYi - y,
									myXi - x + 1, myYi - y + 1, myX - xf, myY - yf,
									myX - xf + CPlayerFogOfWar.GRID_STEP, myY - yf + CPlayerFogOfWar.GRID_STEP, myZ);
							fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi - x, myYi + y,
									myXi - x + 1, myYi + y - 1, myX - xf, myY + yf,
									myX - xf + CPlayerFogOfWar.GRID_STEP, myY + yf - CPlayerFogOfWar.GRID_STEP, myZ);
							fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi + x, myYi - y,
									myXi + x - 1, myYi - y + 1, myX + xf, myY - yf,
									myX + xf - CPlayerFogOfWar.GRID_STEP, myY - yf + CPlayerFogOfWar.GRID_STEP, myZ);
							fogOfWar.checkDiagonalVision(game, pathingGrid, flying, x, y, myXi + x, myYi + y,
									myXi + x - 1, myYi + y - 1, myX + xf, myY + yf,
									myX + xf - CPlayerFogOfWar.GRID_STEP, myY + yf - CPlayerFogOfWar.GRID_STEP, myZ);
						}
					}
				}
			}
		}
	}
}
