package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.sound;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABActionCreateLoopingSoundEffectOnUnit implements ABAction {

	private ABUnitCallback unit;
	private ABIDCallback id;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		SimulationRenderComponent ret = localStore.game.unitLoopSoundEffectEvent(
				(unit.callback(caster, localStore, castId)), this.id.callback(caster, localStore, castId));
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
	}
}
