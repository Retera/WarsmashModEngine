package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.sound;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABActionCreateSoundEffectAtLocation implements ABAction {

	private ABLocationCallback location;
	private ABIDCallback id;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		AbilityPointTarget loc = location.callback(caster, localStore, castId);
		SimulationRenderComponent ret = localStore.game.locationSoundEffectEvent(loc.getX(), loc.getY(),
				this.id.callback(caster, localStore, castId), false);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
	}
}
