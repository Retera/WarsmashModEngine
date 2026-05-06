package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx.group;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects.ABGroupEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateGroupEffectAtLocation implements ABAction {

	private ABLocationCallback location;
	private ABFloatCallback radius;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final AbilityPointTarget loc = this.location.callback(caster, localStore, castId);
		float rad = radius.callback(caster, localStore, castId);
		War3ID theId = null;
		if (id == null) {
			theId = localStore.get(ABLocalStoreKeys.ALIAS, War3ID.class);
		} else {
			theId = id.callback(caster, localStore, castId);
		}
		final ABGroupEffect ret = new ABGroupEffect(loc, rad, theId, effectType);
		localStore.game.registerTimer(ret);
		localStore.put(ABLocalStoreKeys.LASTCREATEDFX, ret);
	}
}
