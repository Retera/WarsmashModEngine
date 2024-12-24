package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.test;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class CBehaviorChannelTest implements CBehavior {
	private final CUnit unit;
	private int nextArtTick;
	private final CAbilityChannelTest abilityChannelTest;

	public CBehaviorChannelTest(final CUnit unit, final CAbilityChannelTest abilityChannelTest) {
		this.unit = unit;
		this.abilityChannelTest = abilityChannelTest;
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
			game.createTemporarySpellEffectOnUnit(this.unit, this.abilityChannelTest.getAlias(), CEffectType.CASTER);
			this.nextArtTick = gameTurnTick
					+ (int) (this.abilityChannelTest.getArtDuration() / WarsmashConstants.SIMULATION_STEP_TIME);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.channel;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}

}
