package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetAttackTargetLocation extends ABLocationCallback {

	@Override
	public AbilityPointTarget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		AbilityTarget tar = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKTARGET, castId),
				AbilityTarget.class);
		if (tar.visit(AbilityTargetVisitor.POINT) != null) {
			return tar.visit(AbilityTargetVisitor.POINT);
		}
		return new AbilityPointTarget(tar.getX(), tar.getY());
	}

}
