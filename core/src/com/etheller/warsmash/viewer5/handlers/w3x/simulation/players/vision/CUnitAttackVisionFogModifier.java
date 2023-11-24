package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CUnitAttackVisionFogModifier extends CFogModifier {
	private CUnit unit;
	private int playerIndex;

	public CUnitAttackVisionFogModifier(final CUnit unit, final int playerIndex) {
		this.unit = unit;
		this.playerIndex = playerIndex;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}

	@Override
	public void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar) {
		if (!this.unit.isDead() && !this.unit.isHidden() && ATTACKING_UNIT_VISION_RADIUS > 0) {
			final float myX = this.unit.getX();
			final float myY = this.unit.getY();
			final float myZ = this.unit.getUnitType().getMovementType() == MovementType.FLY ? Float.MAX_VALUE
					: game.getTerrainHeight(myX, myY);
			fogOfWar.setState(game.getPathingGrid().getFogOfWarIndexX(myX),
					game.getPathingGrid().getFogOfWarIndexY(myY), (byte) 0);

			int myXi = game.getPathingGrid().getFogOfWarIndexX(myX);
			int myYi = game.getPathingGrid().getFogOfWarIndexX(myY);
			int maxXi = game.getPathingGrid().getFogOfWarIndexX(myX + ATTACKING_UNIT_VISION_RADIUS);
			int maxYi = game.getPathingGrid().getFogOfWarIndexX(myY + ATTACKING_UNIT_VISION_RADIUS);
			for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
				int distance = a * a;
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& myZ >= game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)) {
					fogOfWar.setState(myXi, myYi - a, (byte) 0);
				}
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& myZ >= game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)) {
					fogOfWar.setState(myXi, myYi + a, (byte) 0);
				}
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& myZ >= game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)) {
					fogOfWar.setState(myXi - a, myYi, (byte) 0);
				}
				if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ
						&& myZ >= game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)) {
					fogOfWar.setState(myXi + a, myYi, (byte) 0);
				}
			}

			for (int y = 1; y <= maxYi - myYi; y++) {
				for (int x = 1; x <= maxXi - myXi; x++) {
					float distance = x * x + y * y;
					if (distance <= ATTACKING_UNIT_VISION_RADIUS_SQ) {
						int xf = x * CPlayerFogOfWar.GRID_STEP;
						int yf = y * CPlayerFogOfWar.GRID_STEP;

						if (myZ >= game.getTerrainHeight(myX - xf, myY - yf)
								&& fogOfWar.getState(myXi - x + 1, myYi - y + 1) == 0
								&& (x == y || (x > y && fogOfWar.getState(myXi - x + 1, myYi - y) == 0)
										|| (x < y && fogOfWar.getState(myXi - x, myYi - y + 1) == 0))) {
							fogOfWar.setState(myXi - x, myYi - y, (byte) 0);
						}
						if (myZ >= game.getTerrainHeight(myX - xf, myY + yf)
								&& fogOfWar.getState(myXi - x + 1, myYi + y - 1) == 0
								&& (x == y || (x > y && fogOfWar.getState(myXi - x + 1, myYi + y) == 0)
										|| (x < y && fogOfWar.getState(myXi - x, myYi + y - 1) == 0))) {
							fogOfWar.setState(myXi - x, myYi + y, (byte) 0);
						}
						if (myZ >= game.getTerrainHeight(myX + xf, myY - yf)
								&& fogOfWar.getState(myXi + x - 1, myYi - y + 1) == 0
								&& (x == y || (x > y && fogOfWar.getState(myXi + x - 1, myYi - y) == 0)
										|| (x < y && fogOfWar.getState(myXi + x, myYi - y + 1) == 0))) {
							fogOfWar.setState(myXi + x, myYi - y, (byte) 0);
						}
						if (myZ >= game.getTerrainHeight(myX + xf, myY + yf)
								&& fogOfWar.getState(myXi + x - 1, myYi + y - 1) == 0
								&& (x == y || (x > y && fogOfWar.getState(myXi + x - 1, myYi + y) == 0)
										|| (x < y && fogOfWar.getState(myXi + x, myYi + y - 1) == 0))) {
							fogOfWar.setState(myXi + x, myYi + y, (byte) 0);
						}
					}
				}
			}
		}
	}
}
