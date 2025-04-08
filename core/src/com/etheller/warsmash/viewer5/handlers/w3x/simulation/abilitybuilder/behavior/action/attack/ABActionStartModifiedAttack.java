
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.attack;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.attack.ABAttackModifierCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionStartModifiedAttack implements ABAction {

	private ABUnitCallback unit;
	private ABUnitCallback target;
	private ABAttackModifierCallback modifier;

	private ABBooleanCallback disableMove;
	
	private ABBooleanCallback stopOnFailure;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(game, caster, localStore, castId);
		}
		CUnit theTarget = target.callback(game, caster, localStore, castId);

		boolean isDisableMove = false;
		if (disableMove != null) {
			isDisableMove = disableMove.callback(game, caster, localStore, castId);
		}

		for (final CUnitAttack attack : theUnit.getCurrentAttacks()) {
			if (theUnit.canReach(theTarget, theUnit.getAcquisitionRange())
					&& theTarget.canBeTargetedBy(game, theUnit, attack.getTargetsAllowed())
					&& (theUnit.distance(theTarget) >= theUnit.getUnitType().getMinimumAttackRange())) {
				if (!theTarget.isImmuneToDamage(game, null, attack.getAttackType(),
						attack.getWeaponType().getDamageType())) {
					theUnit.beginBehavior(game,
							theUnit.getAttackBehavior().reset(game, OrderIds.attack, attack, theTarget, isDisableMove,
									CBehaviorAttackListener.DO_NOTHING,
									modifier.callback(game, caster, localStore, castId)));
					return;
				}
			}
		}
		boolean stop = true;
		if (stopOnFailure != null) {
			stop = stopOnFailure.callback(game, caster, localStore, castId);
		}
		if (stop) {
			theUnit.performDefaultBehavior(game);
			game.spawnTextTag(theUnit, theUnit.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss");
		}
	}
}