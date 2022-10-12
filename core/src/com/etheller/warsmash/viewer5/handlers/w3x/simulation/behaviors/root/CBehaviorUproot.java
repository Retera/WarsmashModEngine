package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorUproot implements CBehavior {
	private final CUnit unit;
	private final CAbilityRoot abilityRoot;
	private int finishTick;

	public CBehaviorUproot(CUnit unit, CAbilityRoot abilityRoot) {
		this.unit = unit;
		this.abilityRoot = abilityRoot;
	}

	public CBehavior reset() {
		finishTick = -1;
		return this;
	}

	@Override
	public CBehavior update(CSimulation game) {
		float duration = abilityRoot.getOffDuration();
		if(finishTick == -1) {
			finishTick = game.getGameTurnTick() + (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
			unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.MORPH, SequenceUtils.EMPTY, duration, true);
		}
		else if(game.getGameTurnTick() >= finishTick) {
			abilityRoot.setRooted(false, unit, game);
			unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
			unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.ALTERNATE);
			return unit.pollNextOrderBehavior(game);
		}
		return this;
	}

	@Override
	public void begin(CSimulation game) {
		
	}

	@Override
	public void end(CSimulation game, boolean interrupted) {
		
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.unroot;
	}

}
