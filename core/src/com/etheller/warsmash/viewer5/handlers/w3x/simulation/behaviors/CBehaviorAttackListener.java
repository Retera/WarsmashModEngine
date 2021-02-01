package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;

public interface CBehaviorAttackListener extends CUnitAttackListener {

	// For this function, return the current attack behavior to keep attacking, or
	// else return something else to interrupt it
	CBehavior onFirstUpdateAfterBackswing(CBehaviorAttack currentAttackBehavior);

	CBehavior onFinish(CSimulation game, final CUnit finishingUnit);

	CBehaviorAttackListener DO_NOTHING = new CBehaviorAttackListener() {
		@Override
		public void onHit(final AbilityTarget target, final float damage) {
		}

		@Override
		public void onLaunch() {
		}

		@Override
		public CBehavior onFirstUpdateAfterBackswing(final CBehaviorAttack currentAttackBehavior) {
			return currentAttackBehavior;
		}

		@Override
		public CBehavior onFinish(final CSimulation game, final CUnit finishingUnit) {
			return finishingUnit.pollNextOrderBehavior(game);
		}
	};
}
