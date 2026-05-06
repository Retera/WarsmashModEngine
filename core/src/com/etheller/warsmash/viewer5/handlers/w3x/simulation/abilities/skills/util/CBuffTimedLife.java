package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CBuffTimedLife extends CBuffTimed {

	private final boolean explode;

	public CBuffTimedLife(final int handleId, final War3ID alias, final float duration, boolean explode) {
		super(handleId, alias, alias, duration);
		this.explode = explode;
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {
		if (this.explode) {
			unit.setExplodesOnDeath(true);
			unit.setExplodesOnDeathBuffId(getAlias());
		}

		final CPlayer player = game.getPlayer(unit.getPlayerIndex());
		player.setUnitFoodMade(unit, 0);
		player.setUnitFoodUsed(unit, 0);
	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {
		unit.kill(game);
	}

	@Override
	public boolean isTimedLifeBar() {
		return true;
	}
}
