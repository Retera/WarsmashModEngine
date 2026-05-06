package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetAttackedUnit extends ABUnitCallback {

	@Override
	public CUnit callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		AbilityTarget tar = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, castId),
				AbilityTarget.class);
		return tar.visit(AbilityTargetVisitor.UNIT);
	}

}
