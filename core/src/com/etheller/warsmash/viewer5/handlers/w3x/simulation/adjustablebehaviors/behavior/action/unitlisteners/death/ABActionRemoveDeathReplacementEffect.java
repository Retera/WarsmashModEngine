
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.death;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDeathReplacementPriorityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.listener.ABDeathReplacementCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionRemoveDeathReplacementEffect implements ABAction {

	private ABUnitCallback target;
	private ABDeathReplacementPriorityCallback priority;
	private ABDeathReplacementCallback listener;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		target.callback(caster, localStore, castId).removeDeathReplacementEffect(
				priority.callback(caster, localStore, castId), listener.callback(caster, localStore, castId));
	}
}