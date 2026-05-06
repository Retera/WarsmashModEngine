
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.reaction;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.listener.ABAbilityProjReactionListenerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionAddAbilityProjReactionListener implements ABAction {

	private ABUnitCallback targetUnit;
	private ABAbilityProjReactionListenerCallback listener;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnit target = targetUnit.callback(caster, localStore, castId);

		target.addAbilityProjReactionListener(listener.callback(caster, localStore, castId));
	}
}