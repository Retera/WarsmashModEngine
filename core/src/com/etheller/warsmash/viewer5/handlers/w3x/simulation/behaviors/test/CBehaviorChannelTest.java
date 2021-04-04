package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.test;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorChannelTest implements CBehavior {
	private final CUnit unit;
	private final float artDuration;
	private int nextArtTick;

	public CBehaviorChannelTest(final CUnit unit, final float artDuration) {
		this.unit = unit;
		this.artDuration = artDuration;
	}

	public CBehaviorChannelTest reset() {
		this.nextArtTick = 0;
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		this.unit.getUnitAnimationListener().playAnimation(false, null, SequenceUtils.SPELL, 1.0f, true);
		final int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= this.nextArtTick) {
			game.createEffectOnUnit(this.unit, "Abilities\\Spells\\Undead\\DeathPact\\DeathPactTarget.mdl");
			this.nextArtTick = gameTurnTick + (int) (this.artDuration / WarsmashConstants.SIMULATION_STEP_TIME);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.channel;
	}

}
