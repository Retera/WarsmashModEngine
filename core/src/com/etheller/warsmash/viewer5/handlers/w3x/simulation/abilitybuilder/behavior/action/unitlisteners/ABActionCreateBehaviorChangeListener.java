
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABBehaviorChangeListener;

public class ABActionCreateBehaviorChangeListener implements ABAction {

	private List<ABAction> actions;
	private ABBooleanCallback useCastId;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		boolean ucid = true;
		if (useCastId != null) {
			ucid = useCastId.callback(game, caster, localStore, castId);
		}
		ABBehaviorChangeListener listener = new ABBehaviorChangeListener(localStore, actions, castId, ucid);

		localStore.put(ABLocalStoreKeys.LASTCREATEDBCL, listener);
	}
}