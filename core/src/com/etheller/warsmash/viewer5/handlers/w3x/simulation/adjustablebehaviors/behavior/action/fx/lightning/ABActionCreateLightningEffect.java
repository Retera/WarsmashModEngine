package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.lightning;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;

public class ABActionCreateLightningEffect implements ABAction {

	private ABUnitCallback origin;
	private ABUnitCallback target;
	private ABIDCallback id;
	private ABFloatCallback duration;
	private ABIntegerCallback index;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		int i = 0;
		if (index != null) {
			i = index.callback(caster, localStore, castId);
		}
		War3ID theId = null;
		if (id == null) {
			theId = localStore.get(ABLocalStoreKeys.ALIAS, War3ID.class);
		} else {
			theId = id.callback(caster, localStore, castId);
		}
		if (duration != null) {
			SimulationRenderComponentLightning ret = localStore.game.createAbilityLightning(
					origin.callback(caster, localStore, castId), theId, i,
					target.callback(caster, localStore, castId),
					duration.callback(caster, localStore, castId));
			localStore.put(ABLocalStoreKeys.LASTCREATEDLIGHTNING, ret);
		} else {
			SimulationRenderComponentLightning ret = localStore.game.createAbilityLightning(
					origin.callback(caster, localStore, castId), theId, i,
					target.callback(caster, localStore, castId));
			localStore.put(ABLocalStoreKeys.LASTCREATEDLIGHTNING, ret);
		}
	}
}
