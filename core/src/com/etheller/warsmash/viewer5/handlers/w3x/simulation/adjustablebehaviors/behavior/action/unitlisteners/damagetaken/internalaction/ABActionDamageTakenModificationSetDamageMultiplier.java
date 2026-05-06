
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.damagetaken.internalaction;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;

public class ABActionDamageTakenModificationSetDamageMultiplier implements ABAction {

	private ABFloatCallback multiplier;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CDamageCalculation damage = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, castId),
				CDamageCalculation.class);

		damage.setDamageMultiplier(multiplier.callback(caster, localStore, castId));
	}
}