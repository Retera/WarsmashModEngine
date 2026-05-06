package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTemporarySpellEffectAtLocation implements ABSingleAction {

	private ABLocationCallback location;
	private ABFloatCallback facing;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget loc = this.location.callback(caster, localStore, castId);
		float dir = 0;
		if (this.facing != null) {
			dir = this.facing.callback(caster, localStore, castId);
		}
		War3ID theId = null;
		if (id == null) {
			theId = localStore.get(ABLocalStoreKeys.ALIAS, War3ID.class);
		} else {
			theId = id.callback(caster, localStore, castId);
		}
		localStore.game.spawnTemporarySpellEffectOnPoint(loc.getX(), loc.getY(), dir, theId, this.effectType, 0);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		if (this.facing != null) {
			throw new UnsupportedOperationException("AddSpellEffectByIdLoc with facing");
		}
		return "DestroyEffect(AddSpellEffectByIdLoc(" + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", EFFECT_TYPE_" + this.effectType.name() + ", "
				+ this.location.generateJassEquivalent(jassTextGenerator) + ", null))";
	}
}
