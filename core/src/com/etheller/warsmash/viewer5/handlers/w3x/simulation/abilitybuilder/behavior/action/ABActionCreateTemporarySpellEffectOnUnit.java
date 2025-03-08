package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTemporarySpellEffectOnUnit implements ABSingleAction {

	private ABUnitCallback target;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		game.createTemporarySpellEffectOnUnit((this.target.callback(game, caster, localStore, castId)),
				this.id.callback(game, caster, localStore, castId), this.effectType);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "DestroyEffect(AddSpellEffectTargetById(" + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", EFFECT_TYPE_" + this.effectType.name() + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", null))";
	}

}
