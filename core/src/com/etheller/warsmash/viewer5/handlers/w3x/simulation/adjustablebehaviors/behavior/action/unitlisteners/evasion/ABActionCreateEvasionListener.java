
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.evasion;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABAttackEvasionListener;

public class ABActionCreateEvasionListener implements ABAction {

	private List<ABBooleanCallback> conditions;
	private ABBooleanCallback useCastId;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		boolean ucid = true;
		if (useCastId != null) {
			ucid = useCastId.callback(caster, localStore, castId);
		}
		ABAttackEvasionListener listener = new ABAttackEvasionListener(localStore, conditions, castId, ucid);

		localStore.put(ABLocalStoreKeys.LASTCREATEDAEL, listener);
	}
}