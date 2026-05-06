
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier.internal;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitPriorityLoopData;

public class ABActionAttackModifierPreventOtherModifications implements ABAction {

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnitPriorityLoopData loop = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKMODLOOP, castId),
				CUnitPriorityLoopData.class);
		if (loop != null)
			loop.preventAllOtherModifications();
	}
}