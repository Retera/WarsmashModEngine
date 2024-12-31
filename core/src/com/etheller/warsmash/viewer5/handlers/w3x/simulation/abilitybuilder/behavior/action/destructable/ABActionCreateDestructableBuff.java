package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionCreateDestructableBuff implements ABAction {

	private ABIDCallback buffId;
	private List<ABAction> onAddActions;
	private List<ABAction> onRemoveActions;
	private List<ABAction> onDeathActions;

	private ABBooleanCallback dispellable;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		boolean isDispellable = false;
		if (dispellable != null) {
			isDispellable = dispellable.callback(game, caster, localStore, castId);
		}
		CDestructableBuff ability = new ABDestructableBuff(game.getHandleIdAllocator().createId(),
				buffId.callback(game, caster, localStore, castId), (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL),
				localStore, onAddActions, onRemoveActions, onDeathActions, castId, caster, isDispellable);

		localStore.put(ABLocalStoreKeys.LASTCREATEDDESTBUFF, ability);
	}
}
