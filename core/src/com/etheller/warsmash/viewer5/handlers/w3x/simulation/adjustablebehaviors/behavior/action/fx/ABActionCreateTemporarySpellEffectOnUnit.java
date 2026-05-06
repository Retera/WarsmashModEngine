package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.fx;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTemporarySpellEffectOnUnit implements ABSingleAction {

	private ABUnitCallback target;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		War3ID theId = null;
		if (id == null) {
			theId = localStore.get(ABLocalStoreKeys.ALIAS, War3ID.class);
		} else {
			theId = id.callback(caster, localStore, castId);
		}
		localStore.game.createTemporarySpellEffectOnUnit((this.target.callback(caster, localStore, castId)), theId,
				this.effectType);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "DestroyEffect(AddSpellEffectTargetById(" + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", EFFECT_TYPE_" + this.effectType.name() + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", null))";
	}

}
