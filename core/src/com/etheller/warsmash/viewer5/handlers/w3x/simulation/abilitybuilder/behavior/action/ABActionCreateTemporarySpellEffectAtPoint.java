package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateTemporarySpellEffectAtPoint implements ABSingleAction {

	private ABFloatCallback x;
	private ABFloatCallback y;
	private ABFloatCallback facing;
	private ABIDCallback id;
	private CEffectType effectType;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float dir = 0;
		if (this.facing != null) {
			dir = this.facing.callback(game, caster, localStore, castId);
		}
		game.spawnTemporarySpellEffectOnPoint(this.x.callback(game, caster, localStore, castId),
				this.y.callback(game, caster, localStore, castId), dir,
				this.id.callback(game, caster, localStore, castId), this.effectType, 0);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		if (this.facing != null) {
			throw new UnsupportedOperationException("AddSpellEffectByIdLoc with facing");
		}
		return "DestroyEffect(AddSpellEffectById(" + this.id.generateJassEquivalent(jassTextGenerator)
				+ ", EFFECT_TYPE_" + this.effectType.name() + ", " + this.x.generateJassEquivalent(jassTextGenerator)
				+ ", " + this.y.generateJassEquivalent(jassTextGenerator) + ", null))";
	}
}
