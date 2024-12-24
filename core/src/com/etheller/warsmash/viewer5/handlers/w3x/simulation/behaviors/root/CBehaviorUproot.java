package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorUproot implements CBehavior {
	private final CUnit unit;
	private final CAbilityRoot abilityRoot;
	private int finishTick;

	public CBehaviorUproot(final CUnit unit, final CAbilityRoot abilityRoot) {
		this.unit = unit;
		this.abilityRoot = abilityRoot;
	}

	public CBehavior reset() {
		this.finishTick = -1;
		return this;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		final float duration = this.abilityRoot.getOffDuration();
		if (this.finishTick == -1) {
			this.finishTick = game.getGameTurnTick() + (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
			this.unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.MORPH, SequenceUtils.EMPTY,
					duration, true);
			this.unit.setAcceptingOrders(false);
		}
		else if (game.getGameTurnTick() >= this.finishTick) {
			this.abilityRoot.setRooted(false, this.unit, game);
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
					true);
			if (this.unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE)) {
				this.unit.getUnitAnimationListener().forceResetCurrentAnimation();
			}
			this.unit.setAcceptingOrders(true);
			return this.unit.pollNextOrderBehavior(game);
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
		return OrderIds.unroot;
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
		return CBehaviorCategory.MOVEMENT;
	}
}
