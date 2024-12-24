package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.eattree;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;

public class CBuffEatTree extends CBuffTimed {
	private final float delay;
	private final float duration;
	private float hitPointsGained;
	private final float healPerTick;

	private int nextHealTick;

	public CBuffEatTree(final int handleId, final War3ID alias, final float delay, final float duration,
			final float hitPointsGained) {
		super(handleId, alias, alias, delay + duration);
		this.delay = delay;
		this.duration = duration;
		this.hitPointsGained = hitPointsGained;
		healPerTick = hitPointsGained / (duration / delay);
		this.nextHealTick = 0;
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

	@Override
	protected void onBuffAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	protected void onBuffRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit caster) {
		if (!this.isDisabled()) {
			final int gameTurnTick = game.getGameTurnTick();
			if (gameTurnTick >= nextHealTick) {
				if ((nextHealTick != 0) && (hitPointsGained > 0)) {
					final float healingDone = Math.min(healPerTick, hitPointsGained);
					caster.heal(game, healingDone);
					hitPointsGained -= healingDone;
				}
				nextHealTick = gameTurnTick + (int) (delay / WarsmashConstants.SIMULATION_STEP_TIME);
			}
		}
		super.onTick(game, caster);
	}
}
