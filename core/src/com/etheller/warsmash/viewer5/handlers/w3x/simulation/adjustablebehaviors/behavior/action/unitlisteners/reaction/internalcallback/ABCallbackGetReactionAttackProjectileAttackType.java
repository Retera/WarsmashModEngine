package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.reaction.internalcallback;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;

public class ABCallbackGetReactionAttackProjectileAttackType extends ABAttackTypeCallback {

	@Override
	public CAttackType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ATTACKPROJ, castId), CAttackProjectile.class))
				.getUnitAttack().getAttackType();
	}

}
