
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.attack.ABAttackModifierCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
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

	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(caster, localStore, castId);
		}
		CUnit theTarget = target.callback(caster, localStore, castId);

		boolean isDisableMove = false;
		if (disableMove != null) {
			isDisableMove = disableMove.callback(caster, localStore, castId);
		}

		for (final CUnitAttack attack : theUnit.getCurrentAttacks()) {
			if (theUnit.canReach(theTarget, theUnit.getAcquisitionRange())
					&& theTarget.canBeTargetedBy(localStore.game, theUnit, attack.getTargetsAllowed())
					&& (theUnit.distance(theTarget) >= theUnit.getUnitType().getMinimumAttackRange())) {
				if (!theTarget.isImmuneToDamage(localStore.game, null, attack.getAttackType(),
						attack.getWeaponType().getDamageType())) {
					theUnit.beginBehavior(localStore.game,
							theUnit.getAttackBehavior().reset(localStore.game, OrderIds.attack, attack, theTarget, isDisableMove,
									CBehaviorAttackListener.DO_NOTHING,
									modifier.callback(caster, localStore, castId)), true);
					return;
				}
			}
		}
		boolean stop = true;
		if (stopOnFailure != null) {
			stop = stopOnFailure.callback(caster, localStore, castId);
		}
		if (stop) {
			theUnit.performDefaultBehavior(localStore.game);
			localStore.game.spawnTextTag(theUnit, theUnit.getPlayerIndex(), TextTagConfigType.CRITICAL_STRIKE, "miss");
		}
	}
}