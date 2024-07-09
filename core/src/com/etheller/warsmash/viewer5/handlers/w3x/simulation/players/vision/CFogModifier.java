package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CGameplayConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public abstract class CFogModifier {
	public static float FOG_UPDATE_TIME = 1f;

	public static float DYING_UNIT_VISION_RADIUS = 0;
	public static float DYING_UNIT_VISION_RADIUS_SQ = 0;
	public static float DYING_UNIT_VISION_DURATION = 0;
	public static float ATTACKING_UNIT_VISION_RADIUS = 0;
	public static float ATTACKING_UNIT_VISION_RADIUS_SQ = 0;

	public static void setConstants(CGameplayConstants constants) {
		DYING_UNIT_VISION_RADIUS = constants.getDyingRevealRadius();
		DYING_UNIT_VISION_RADIUS_SQ = (DYING_UNIT_VISION_RADIUS * DYING_UNIT_VISION_RADIUS)
				/ (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
		DYING_UNIT_VISION_DURATION = constants.getFogFlashTime();
		ATTACKING_UNIT_VISION_RADIUS = constants.getFoggedAttackRevealRadius();
		ATTACKING_UNIT_VISION_RADIUS_SQ = (ATTACKING_UNIT_VISION_RADIUS * ATTACKING_UNIT_VISION_RADIUS)
				/ (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
	}

	public void setEnabled(final boolean enabled) {
	}

	public void onAdd(final CSimulation game, final CPlayer player) {
	}

	public void onRemove(final CSimulation game, final CPlayer player) {
	}

	public abstract void update(final CSimulation game, final CPlayer player, final PathingGrid pathingGrid,
			final CPlayerFogOfWar fogOfWar);
}
