
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.attack.internalaction;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;

public class ABActionSetPreDamageStacking implements ABAction {

	private ABBooleanCallback allowStacking;
	private ABBooleanCallback allowSamePriorityStacking;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CDamageCalculation damage = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, castId),
				CDamageCalculation.class);

		if (allowStacking != null && !allowStacking.callback(caster, localStore, castId)) {
			damage.preventAllOtherModifications();
		}
		if (allowSamePriorityStacking != null && !allowSamePriorityStacking.callback(caster, localStore, castId)) {
			damage.preventOtherModificationsWithSamePriority();
		}
	}
}