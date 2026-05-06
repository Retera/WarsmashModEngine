package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRandomLocationInRange extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback range;

	@Override
	public AbilityPointTarget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget orig = this.origin.callback(caster, localStore, castId);
		final float d = this.range.callback(caster, localStore, castId) * localStore.game.getSeededRandom().nextFloat();
		final float a = (float) (Math.PI * 2 * localStore.game.getSeededRandom().nextFloat());

		orig.add((float) (d * Math.cos(a)), (float) (d * Math.sin(a)));
		return orig;
	}

}
